// Change product image when thumbnail is clicked
function changeImage(element) {
    const mainImage = document.getElementById('mainProductImage');
    mainImage.src = element.src;

    // Update active thumbnail
    const thumbnails = document.querySelectorAll('.product-thumbnail');
    thumbnails.forEach(thumb => thumb.classList.remove('active'));
    element.parentElement.classList.add('active');
}

//button
//document.querySelectorAll('.btn-plus').forEach(button => {
//    button.addEventListener('click', function () {
//        let input = document.getElementById("add-to-cart-qty");
//        let max = document.getElementById("product-display-qty").value;
//        alert(max);
//        input.value = parseInt(input.value) + 1;
//    });
//});
//
//document.querySelectorAll('.btn-minus').forEach(button => {
//    button.addEventListener('click', function () {
//        let input = document.getElementById("add-to-cart-qty");
//        if (parseInt(input.value) > 1) {
//            input.value = parseInt(input.value) - 1;
//        }
//    });
//});
////////////////////////////////////Functions////////////////////////////////////////////

//loaddata
async function loadData() {
    const searchParams = new URLSearchParams(window.location.search);
    if (searchParams.has("id")) {
        const productId = searchParams.get("id");
        const response = await fetch("LoadSigleProduct?id=" + productId);

        if (response.ok) {
            const json = await response.json();
            console.log(json);

            // Main product image setup
            document.getElementById("mainProductImage").src = `product-images/${json.product.id}/image1.png`;
            document.getElementById("image1").src = `product-images\\${json.product.id}\\image1.png`;
            document.getElementById("image2").src = `product-images\\${json.product.id}\\image2.png`;
            document.getElementById("image3").src = `product-images\\${json.product.id}\\image3.png`;

            // Product details
            document.getElementById("product-title").innerHTML = json.product.title;
            document.getElementById("product-price").innerHTML = new Intl.NumberFormat("en-Us", {minimumFractionDigits: 2}).format(json.product.price);
            document.getElementById("product-display-qty").innerHTML = json.product.qty;
            document.getElementById("product-description").innerHTML = json.product.description;
            document.getElementById("product-cat").innerHTML = json.product.category.value;
            document.getElementById("product-volume").innerHTML = json.product.volumn.value;

            // Add to Cart main button
            const addToCartMain = document.getElementById("add-to-cart-main");
            addToCartMain.addEventListener("click", (e) => {
                e.preventDefault();
                const qty = parseInt(document.getElementById("add-to-cart-qty").value) || 1;
                addToCart(json.product.id, qty);
            });

            // Product intro (infograph)
            const paragraphs = json.product.description.split(/\r?\n\r?\n/);
            document.querySelector("#product-intro p").innerHTML = paragraphs[0];

            // Related products
            const smilerProductMain = document.getElementById("related-products");
            smilerProductMain.innerHTML = "";

            if (json.ratings.length > 0) {
                document.getElementById("rating-number").innerHTML = (json.ratings[0].rating || 0) + "/10";
            } else {
                document.getElementById("rating-number").innerHTML = "0/10";
            }
            let maxQTYcount = json.product.qty;
            json.productList.forEach(item => {
                //  document.getElementById("rating-number").innerHTML = rating + "/10";
                const productCloneHtml = `
                    <div class="col-md-3 mb-4">
                        <div class="card h-100">
                            <a href="Single-Product.html?id=${item.id}">
                                <img src="product-images/${item.id}/image1.png" class="card-img-top" alt="Product Image">
                                <div class="card-body">
                                    <h5 class="card-title">${item.title}</h5>
                                    <p class="card-text">Rs. ${item.price}</p>
                                </div>
                            </a>
                            <div class="card-footer bg-transparent">
                                <button class="btn btn-primary btn-sm w-100 similer-product-add-to-cart" data-id="${item.id}">
                                    Add to Cart
                                </button>
                            </div>
                        </div>
                    </div>`;
                smilerProductMain.innerHTML += productCloneHtml;

            });
            // Attach Add to Cart for similar product buttons
            document.querySelectorAll(".similer-product-add-to-cart").forEach(button => {
                button.addEventListener("click", (e) => {
                    e.preventDefault();
                    const simProductId = button.getAttribute("data-id");
                    addToCart(simProductId, 1); // default quantity = 1
                });
            });

            const reviewsContainer = document.querySelector("#reviews");
            reviewsContainer.innerHTML = `<h4 class="card-title mb-4">Customer Reviews</h4>`;

            if (json.ratings.length > 0) {
                json.ratings.forEach(r => {
                    const reviewHtml = `
            <div class="mb-4">
                <div class="d-flex justify-content-between mb-2">
                    <h5>${r.comment || "No Title"}</h5>
                    <div class="rating-number">${r.rating || 0}/10</div>
                </div>
                <p class="text-muted">By ${r.orders?.user?.firstName || "Anonymous"} ${r.orders?.user?.lastName || ""}</p>
                <p>${r.orders.createdAt || ""}</p>
            </div>
        `;
                    reviewsContainer.innerHTML += reviewHtml;
                });
            } else {
                reviewsContainer.innerHTML += `<p class="text-muted">No reviews yet.</p>`;
            }

            document.querySelectorAll('.btn-plus').forEach(button => {
                button.addEventListener('click', function () {
                    let input = document.getElementById("add-to-cart-qty");
                    let currentValue = parseInt(input.value) || 1;

                    if (currentValue < maxQTYcount) {
                        input.value = currentValue + 1;
                    } else {
                        Toastify({
                            text: "You’ve reached the maximum available quantity.",
                            className: "info",
                            style: {
                                background: "linear-gradient(to right, #ec008c, #fc6767)",
                            }
                        }).showToast();
                    }
                });
            });

            document.querySelectorAll('.btn-minus').forEach(button => {
                button.addEventListener('click', function () {
                    let input = document.getElementById("add-to-cart-qty");
                    let currentValue = parseInt(input.value) || 1;

                    if (currentValue > 1) {
                        input.value = currentValue - 1;
                    }
                });
            });

        }
    }
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




