function loadData() {
    MyCart();
    loadPopularItems();
    loadHomeItems();
    loadProfileData();
}

async function loadProfileData() {
    const response = await fetch("loadUserData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            document.getElementById("topImg").src = json.file;
            document.getElementById("admin").innerHTML = json.ProfileName;
        } else {
            alert("error");
        }
    }
}

async function MyCart() {
    const response = await fetch("MyCart");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json);
            const cartItemList = document.getElementById("cart-items-list");
            cartItemList.innerHTML = ""; // Clear only the product list
            let total = 0;
            let totalQty = 0;

            json.cartItems.forEach(cart => {
                const productSubTotal = cart.product.price * cart.qty;
                total += productSubTotal;
                totalQty += cart.qty;

                const itemHtml = `
                    <div class="list-group-item p-2">
                        <div class="d-flex justify-content-between">
                            <div class="d-flex">
                                <img src="product-images\\${cart.product.id}\\image1.png" class="rounded me-3" alt="Product" style="width: 80px; height: 80px; object-fit: cover;">
                                <div>
                                    <h6 class="mb-1">${cart.product.title}</h6>
                                    <small class="text-muted">${cart.product.volumn.value}</small>
                                    <div class="input-group input-group-sm mt-2" style="width: 100px;">
                                        <button class="btn btn-outline-secondary" type="button">-</button>
                                        <input type="text" class="form-control text-center" value="${cart.qty}" readonly>
                                        <button class="btn btn-outline-secondary" type="button">+</button>
                                    </div>
                                </div>
                            </div>
                            <div class="text-end">
                                <div class="fw-bold">Rs. ${cart.product.price.toFixed(2)}</div>
                                <button class="btn btn-sm btn-link text-danger p-0" onclick="trash(${cart.product.id});"><i class="fas fa-trash"></i></button>
                            </div>
                        </div>
                    </div>`;

                cartItemList.innerHTML += itemHtml;
            });

            // Update totals
            document.getElementById("items").textContent = `(${totalQty})`;
            document.getElementById("subtotal").textContent = `Rs. ${total.toFixed(2)}`;
            document.getElementById("total").textContent = `Rs. ${total.toFixed(2)}`;
        } else {
            console.log("Cart loading failed.");
        }
    } else {
        console.error("Network error:", await response.text());
    }
}


async function trash(id) {
    const response = await fetch("DeleteCart?id=" + id);

    if (response.ok) {

        const json = await response.json();

        if (json.status) {

            swal("Success!", json.message, "success").then(() => {
                window.location.reload();
            });


        } else {
            swal("Error!", json.message, "error").then(() => {
                window.location.reload();
            });
        }


    } else {
        swal("Error!", json.message, "error").then(() => {
            window.location.reload();
        });
    }

}


async function loadPopularItems() {

    const response = await fetch("loadPopularItems");

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            let templateContainer = document.getElementById("popularContainer");
            templateContainer.innerHTML = "";

            json.popular_products.forEach(items => {
                let tableData = `
                <div class="col-md-6 col-lg-3" >
                        <div class="product-card bg-white p-3 h-100">
                            <div class="badge bg-danger position-absolute" style="top: 10px; right: 10px;">Hot Deals</div>
                            <img src="product-images/${items.product_id}/image1.png" class="img-fluid mb-3" alt="Product" onclick="window.location = 'Single-Product.html?id=${items.product_id}'">
                            <h5>${items.product_name}</h5>
                            <p class="text-muted small">${items.product_category}</p>
                            <div class="d-flex justify-content-between align-items-center">
                                <div>
                                    <span class="fw-bold text-florish">Rs.${items.product_price}.00</span>
                                </div>
                                <button class="btn btn-sm btn-florish" onclick="window.location = 'Single-Product.html?id=${items.product_id}'"><i class="fas fa-eye"></i> View Product</button>
                            </div>
                        </div>
                    </div>
                `;
                templateContainer.innerHTML += tableData;
            });
        } else {
            console.log(json.message);
        }
    } else {
        console.log(response.text());
    }
}
async function loadHomeItems(firstResult = 0) {
    const response = await fetch("LoadHomePageProducts?finalres=" + firstResult);

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            console.log(json);
            let templateContainer = document.getElementById("homeProducts");
            templateContainer.innerHTML = "";

            json.popular_products.forEach(item => {
                const maxLength = 50; 
                const truncatedDesc = item.description
                        ? (item.description.length > maxLength
                                ? `${item.description.substring(0, maxLength)}...`
                                : item.description)
                        : 'No description available';
                let tableData = `
                    <div class="col-md-6 col-lg-3" >
                        <div class="product-card bg-white p-3 h-100">
                            <img src="product-images/${item.id}/image1.png" class="img-fluid mb-3" alt="${item.title}" onclick="window.location = 'Single-Product.html?id=${item.id}'">
                            <h5>${item.title}</h5>
                            <p class="text-muted small">${truncatedDesc}</p>
                            <div class="d-flex justify-content-between align-items-center">
                                <div>
                                    <span class="fw-bold text-florish">Rs.${item.price}.00</span>
                                </div>
                                <button class="btn btn-sm btn-florish" onclick="window.location ='Single-Product.html?id=${item.id}' " ><i class="fas fa-eye"></i> View Product</button>
                            </div>
                        </div>
                    </div>`;
                templateContainer.innerHTML += tableData;
            });

            renderPagination(json, Math.floor(firstResult / 4));
        } else {
            console.log(json.message);
        }
    } else {
        console.log(await response.text());
}
}


function renderPagination(json, current_page = 0) {
    const st_pagination_container = document.getElementById("st-pagination-container");
    st_pagination_container.innerHTML = "";

    if (json.allProductCount <= 0) {
        return;
    }

    const ul = document.createElement("ul");
    ul.className = "pagination justify-content-center";

    const all_product_count = json.allProductCount;
    const product_per_page = 6;
    const pages = Math.ceil(all_product_count / product_per_page);

    // Ensure current_page is within bounds
    current_page = Math.max(0, Math.min(current_page, pages - 1));

    //document.getElementById("all-item-count").textContent = all_product_count;

    // Prev button
    const prevLi = document.createElement("li");
    prevLi.className = `page-item ${current_page === 0 ? 'disabled' : ''}`;
    const prevLink = document.createElement("a");
    prevLink.className = "page-link";
    prevLink.href = "#";
    prevLink.textContent = "Previous";
    prevLink.addEventListener("click", (e) => {
        e.preventDefault();
        if (current_page > 0) {
           loadHomeItems((current_page - 1) * product_per_page);
        }
    });
    prevLi.appendChild(prevLink);
    ul.appendChild(prevLi);

    // Numbered page buttons - show limited range if many pages
    const startPage = Math.max(0, current_page - 2);
    const endPage = Math.min(pages - 1, current_page + 2);

    for (let i = startPage; i <= endPage; i++) {
        const pageLi = document.createElement("li");
        pageLi.className = `page-item ${i === current_page ? 'active' : ''}`;
        const pageLink = document.createElement("a");
        pageLink.className = "page-link";
        pageLink.href = "#";
        pageLink.textContent = (i + 1).toString();
        pageLink.addEventListener("click", (e) => {
            e.preventDefault();
            loadHomeItems(i * product_per_page);
        });
        pageLi.appendChild(pageLink);
        ul.appendChild(pageLi);
    }

    // Next button
    const nextLi = document.createElement("li");
    nextLi.className = `page-item ${current_page === pages - 1 ? 'disabled' : ''}`;
    const nextLink = document.createElement("a");
    nextLink.className = "page-link";
    nextLink.href = "#";
    nextLink.textContent = "Next";
    nextLink.addEventListener("click", (e) => {
        e.preventDefault();
        if (current_page < pages - 1) {
            loadHomeItems((current_page + 1) * product_per_page);
        }
    });
    nextLi.appendChild(nextLink);
    ul.appendChild(nextLi);

    st_pagination_container.appendChild(ul);
}


