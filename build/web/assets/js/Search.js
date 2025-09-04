$(document).ready(function () {
    // Price slider initialization - Matching your example
    $("#slider-range").slider({
        range: true,
        min: 0,
        max: 5000,
        values: [0, 5000],
        slide: function (event, ui) {
            $(".amount-range").val("Rs. " + ui.values[0] + " - Rs. " + ui.values[1]);
        }
    });
    $(".amount-range").val("Rs. " + $("#slider-range").slider("values", 0) +
            " - Rs. " + $("#slider-range").slider("values", 1));

    // Volume filter selection
    $(".volume-option").click(function () {
        $(".volume-option").removeClass("selected");
        $(this).addClass("selected");
    });


    // Search button animation
    $('.search-btn').click(function () {
        $(this).html('<i class="fas fa-spinner fa-spin"></i> Searching...');
        setTimeout(function () {
            $('.search-btn').html('<i class="fas fa-search"></i> Search');
        }, 1500);
    });
});

function enforceSingleSelect(className) {
    document.querySelectorAll(`.${className}`).forEach(box => {
        box.addEventListener("change", () => {
            if (box.checked) {
                document.querySelectorAll(`.${className}`).forEach(other => {
                    if (other !== box)
                        other.checked = false;
                });
            }
        });
    });
}

let originalProductCount = 0;

async function loadData() {
    const response = await fetch("LoadData");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json);

            // Store the original product count
            originalProductCount = json.allProductCount;
            document.getElementById("all-item-count").innerHTML = json.allProductCount;

            let categoryList = document.getElementById("div-container");
            let categories = document.getElementById("cat-1");
            categoryList.innerHTML = "";
            json.categoryList.forEach(item => {
                let productCloneHtml = `<div class="form-check mb-2">
        <input class="form-check-input category-checkbox" type="checkbox" name="category" value="${item.value}">
        <label class="form-check-label">${item.value}</label>
    </div>`;
                categoryList.innerHTML += productCloneHtml;
            });

            const container = document.getElementById('div-container');
            const checkboxes = container.querySelectorAll('input[type="checkbox"]');

            checkboxes.forEach(cb => {
                cb.addEventListener('change', function () {
                    if (this.checked) {
                        checkboxes.forEach(other => {
                            if (other !== this)
                                other.checked = false;
                        });
                    }
                });
            });

            let volumeList = document.getElementById("vol-container");
            let volume = document.getElementById("volume");
            volumeList.innerHTML = "";
            json.volumeList.forEach(vol => {
                let productCloneHtml = `<div class="form-check mb-2">
        <input class="form-check-input volume-checkbox" type="checkbox" name="volume" value="${vol.value}">
        <label class="form-check-label">${vol.value}</label>
    </div>`;
                volumeList.innerHTML += productCloneHtml;
            });

            const container2 = document.getElementById('vol-container');
            const checkboxes2 = container2.querySelectorAll('input[type="checkbox"]');

            checkboxes2.forEach(cb2 => {
                cb2.addEventListener('change', function () {
                    if (this.checked) {
                        checkboxes2.forEach(other => {
                            if (other !== this) {
                                other.checked = false;
                            }
                        });
                    }
                });
            });

            updateProductView(json);
            renderPagination(json, 0);
            // AFTER checkboxes are populated dynamically
            enforceSingleSelect("category-checkbox");
            enforceSingleSelect("volume-checkbox");
            enforceSingleSelect("status-checkbox");

        } else {
            Toastify({
                text: json.message,
                className: "info",
                style: {
                    background: "linear-gradient(to right, #00b09b, #96c93d)",
                }
            }).showToast();
        }
    } else {
        Toastify({
            text: json.message,
            className: "info",
            style: {
                background: "linear-gradient(to right, #00b09b, #96c93d)",
            }
        }).showToast();
    }
}

async function loadSearchResults(firstResult = 0) {
    const selectedCategory = document.querySelector("#div-container input[type='checkbox']:checked")?.value;
    const selectedVolume = document.querySelector("#vol-container input[type='checkbox']:checked")?.value;
    const priceStart = $("#slider-range").slider("values", 0);
    const priceEnd = $("#slider-range").slider("values", 1);
    const sortSelect = document.querySelector(".form-select").value;

    const data = {
        firstResult: firstResult,
        priceStart: priceStart,
        priceEnd: priceEnd,
        sort: sortSelect
    };

    if (selectedCategory) {
        data.category = selectedCategory;
    }

    if (selectedVolume) {
        data.volume = selectedVolume;
    }
    console.log("Sending search data:", data);

    try {
        const response = await fetch("SearchProducts", {
            method: "POST",
            body: JSON.stringify(data),
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (response.ok) {
            const json = await response.json();

            if (json.status) {
                // If no filters are applied, use the original count
                if (!selectedCategory && !selectedVolume &&
                        priceStart === 1000 && priceEnd === 5000) {
                    json.allProductCount = originalProductCount;
                }

                updateProductView(json);
                renderPagination(json, Math.floor(firstResult / 6));

                Toastify({
                    text: "Search completed",
                    className: "info",
                    style: {
                        background: "linear-gradient(to right, #00b09b, #96c93d)",
                    }
                }).showToast();

            } else {
                Toastify({
                    text: "No results found.",
                    className: "info",
                    style: {
                        background: "linear-gradient(to right, #00b09b, #96c93d)",
                    }
                }).showToast();
            }
        } else {
            Toastify({
                text: "Server error. Try again later.",
                className: "info",
                style: {
                    background: "linear-gradient(to right, #00b09b, #96c93d)",
                }
            }).showToast();
        }
    } catch (error) {
        console.error(error);
        Toastify({
            text: "Server error. Try again later.",
            className: "info",
            style: {
                background: "linear-gradient(to right, #00b09b, #96c93d)",
            }
        }).showToast();
}
}

function updateProductView(json) {
    const product_container = document.getElementById("img-container");
    product_container.innerHTML = "";

    let rowHTML = "";

    json.productList.forEach((product, index) => {
        if (index % 3 === 0) {
            if (index !== 0) {
                rowHTML += '</div>';
            }
            rowHTML += '<div class="row mb-4">';
        }

        rowHTML += `
            <div class="col-12 col-sm-6 col-md-4 d-flex justify-content-center mb-3">
                <div class="card search-card">
                    <a href="Single-Product.html?id=${product.id}">
                        <img src="product-images\\${product.id}\\image1.png"
                             class="card-img-top fixed-img" alt="Product Image">
                        <div class="card-body">
                            <h5 class="card-title">${product.title}</h5>
                            <p class="card-text text-success fw-bold">Rs.
                                ${new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(product.price)}
                            </p>
                        </div>
                    </a>
                    <div class="card-footer bg-transparent">
                        <button class="btn btn-primary btn-sm w-100" onclick="addToCart(${product.id}, 1)">Add to Cart</button>
                    </div>
                </div>
            </div>`;
    });

    if (json.productList.length > 0) {
        rowHTML += '</div>';
    }

    product_container.innerHTML = rowHTML;
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

    document.getElementById("all-item-count").textContent = all_product_count;

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
            loadSearchResults((current_page - 1) * product_per_page);
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
            loadSearchResults(i * product_per_page);
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
            loadSearchResults((current_page + 1) * product_per_page);
        }
    });
    nextLi.appendChild(nextLink);
    ul.appendChild(nextLi);

    st_pagination_container.appendChild(ul);
}


async function addToCart(productId, qty) {
    const response = await fetch("AddToCart?prId=" + productId + "&qty=" + qty); //await response.text();

    if (response.ok) {

        const json = await response.json();

        if (json.status) {

            Toastify({
                text: json.message,
                className: "info",
                style: {
                    background: "linear-gradient(to right, #00b09b, #96c93d)",
                }
            }).showToast();

        } else {
            Toastify({
                text: json.message,
                className: "info",
                style: {
                    background: "linear-gradient(to right, #ec008c, #fc6767)",
                }
            }).showToast();
        }

    } else {
        Toastify({
            text: json.message,
            className: "info",
            style: {
                background: "linear-gradient(to right, #ec008c, #fc6767)",
            }
        }).showToast();
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
                                <img src="product-images\\${cart.product.id}\\image1.png" class="rounded me-3" alt="Product" style="width: 80px; height: 80px; object-fit: cover;" onclick="window.location = 'Single-Product.html?id=${cart.product.id}' ">
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
    //alert(id);
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











