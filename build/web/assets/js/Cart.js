$(document).ready(function () {
    // Quantity input controls
    $('.quantity-input').on('change', function () {
        if ($(this).val() < 1) {
            $(this).val(1);
        }
        updateCartTotals();
    });

    // Remove item from cart
    $('.remove-btn').click(function () {
        $(this).closest('tr').fadeOut(300, function () {
            $(this).remove();
            updateCartTotals();
            checkEmptyCart();
        });
    });

    // Update cart totals
    function updateCartTotals() {
        let subtotal = 0;

        $('tbody tr').each(function () {
            const price = parseFloat($(this).find('td:nth-child(2)').text().replace('Rs. ', '').replace(',', ''));
            const quantity = parseInt($(this).find('.quantity-input').val());
            const total = price * quantity;

            $(this).find('td:nth-child(4)').text('Rs. ' + total.toLocaleString('en-US', {minimumFractionDigits: 2}));
            subtotal += total;
        });

        const shipping = 1000;
        const tax = subtotal * 0.02; // 2% tax
        const total = subtotal + shipping + tax;

        $('.summary-row:nth-child(1) span:nth-child(2)').text('Rs. ' + subtotal.toLocaleString('en-US', {minimumFractionDigits: 2}));
        $('.summary-row:nth-child(3) span:nth-child(2)').text('Rs. ' + tax.toLocaleString('en-US', {minimumFractionDigits: 2}));
        $('.summary-total:nth-child(2)').text('Rs. ' + total.toLocaleString('en-US', {minimumFractionDigits: 2}));
    }

    // Check if cart is empty
    function checkEmptyCart() {
        if ($('tbody tr').length === 0) {
            $('.cart-table, .summary-card').addClass('d-none');
            $('.empty-cart').removeClass('d-none');
        }
    }
});


async function loadCartItems() {
    const response = await fetch("LoadCartItems");

    if (response.ok) {
        const json = await response.json();
        console.log(json);
        if (json.status) {
            document.querySelector("#empty-cart").classList.add("d-none");
            document.querySelector(".cart-table").classList.remove("d-none");
            document.querySelector(".continue-btn").classList.remove("d-none");
            document.querySelector("#order-summery").classList.remove("d-none");
            const cart_item_container = document.getElementById("cart-item-container");
            cart_item_container.innerHTML = "";
            let total = 0;
            let totalQty = 0;
            json.cartItems.forEach(cart => {
                let productSubTotal = cart.product.price * cart.qty;
                total += productSubTotal;
                totalQty += cart.qty;
                let tableData = `<tr class="cart-item" id="cart-item-row">
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <img src="product-images\\${cart.product.id}\\image1.png" class="product-img me-3" alt="Product Image" onclick="window.location = 'Single-Product.html?id=${cart.product.id}' ">
                                            <div>
                                                <h6 class="mb-1">${cart.product.title}</h6>
                                                <p class="text-muted mb-0">${cart.product.volumn.value}</p>
                                            </div>
                                        </div>
                                    </td>
                                    <td>Rs. ${new Intl.NumberFormat(
                        "en-Us",
                        {minimumFractionDigits: 2})
                        .format(cart.product.price)}</td>
                                    <td>
                                        <input type="number" class="quantity-input" value="${cart.qty}" min="1" disabled>
                                    </td>
                                    <td>Rs. ${new Intl.NumberFormat(
                        "en-Us",
                        {minimumFractionDigits: 2})
                        .format(productSubTotal)}</td>
                                    <td>
                                        <button class="remove-btn" onclick="trash(${cart.product.id})">
                                            <i class="far fa-trash-alt"></i>
                                        </button>
                                    </td>
                                </tr>`;

                cart_item_container.innerHTML += tableData;
            });

            document.getElementById("order-total-quantity").innerHTML = totalQty;
            document.getElementById("order-total-amount").innerHTML = new Intl.NumberFormat(
                    "en-Us",
                    {minimumFractionDigits: 2})
                    .format(total);
            document.getElementById("order-total-amount-p").innerHTML = new Intl.NumberFormat(
                    "en-Us",
                    {minimumFractionDigits: 2})
                    .format(total);


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
        document.getElementById("empty-cart").style.display = "block";
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

            json.cartItems.forEach(Item => {
                const productSubTotal = Item.product.price * Item.qty;
                total += productSubTotal;
                totalQty += Item.qty;

                const itemHtml = `
                    <div class="list-group-item p-2">
                        <div class="d-flex justify-content-between">
                            <div class="d-flex">
                                <img src="product-images\\${Item.product.id}\\image1.png" class="rounded me-3" alt="Product" style="width: 80px; height: 80px; object-fit: cover;" onclick="window.location = 'Single-Product.html?id=${Item.product.id}' ">
                                <div>
                                    <h6 class="mb-1">${Item.product.title}</h6>
                                    <small class="text-muted">${Item.product.volumn.value}</small>
                                    <div class="input-group input-group-sm mt-2" style="width: 100px;">
                                        <button class="btn btn-outline-secondary" type="button">-</button>
                                        <input type="text" class="form-control text-center" value="${Item.qty}" readonly>
                                        <button class="btn btn-outline-secondary" type="button">+</button>
                                    </div>
                                </div>
                            </div>
                            <div class="text-end">
                                <div class="fw-bold">Rs. ${Item.product.price.toFixed(2)}</div>
                                <button class="btn btn-sm btn-link text-danger p-0" onclick="trash(${Item.product.id});"><i class="fas fa-trash"></i></button>
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
            Toastify({
                text: json.message,
                className: "info",
                style: {
                    background: "linear-gradient(to right, #00b09b, #96c93d)",
                },
                duration: 2000,
                stopOnFocus: true
            }).showToast();

            setTimeout(() => {
                window.location.reload();
            }, 2000);

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
            text: response.text(),
            className: "info",
            style: {
                background: "linear-gradient(to right, #ec008c, #fc6767)",
            }
        }).showToast();
    }

}

