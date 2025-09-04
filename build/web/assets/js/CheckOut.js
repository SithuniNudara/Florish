// Payment completed. It can be a successful failure.
payhere.onCompleted = function onCompleted(orderId) {
    
     swal("Success!", "Payment completed. OrderID:" + orderId, "success").then(() => {
        window.location = "index.html";
    });
};

// Payment window closed
payhere.onDismissed = function onDismissed() {
    // Note: Prompt user to pay again or show an error page
    console.log("Payment dismissed");
    swal("Error!", "Payment dismissed", "error").then(() => {
        window.location.reload();
    });
};

// Error occurred
payhere.onError = function onError(error) {
    // Note: show an error page
    console.log("Error:" + error);
     swal("Error!", error, "error").then(() => {
        window.location.reload();
    });
};

async function loadCheckOutData() {

    const response = await fetch("LoadCheckOutData");

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            console.log(json);

            const userAddress = json.userAddress;
            const cityList = json.cityList;
            const cartItems = json.cartList;
            const deliveryTypes = json.deliveryTypes;

            let city_select = document.getElementById("city-select");

            cityList.forEach(city => {
                let option = document.createElement("option");
                option.value = city.id;
                option.innerHTML = city.name;
                city_select.appendChild(option);
            });

            const current_address_checkbox = document.getElementById("checkbox1");
            current_address_checkbox.addEventListener("change", function () {

                let first_name = document.getElementById("firstName");
                let last_name = document.getElementById("lastName");
                let mobile = document.getElementById("phone");
                let line_one = document.getElementById("line1");
                let line_two = document.getElementById("line2");
                let postal_code = document.getElementById("postalCode");

                if (current_address_checkbox.checked) {
                    first_name.value = userAddress.user.firstName;
                    last_name.value = userAddress.user.lastName;
                    city_select.value = userAddress.city.id;
                    city_select.disabled = true;
                    city_select.dispatchEvent(new Event("change"));
                    line_one.value = userAddress.line1;
                    line_two.value = userAddress.line2;
                    postal_code.value = userAddress.postalCode;
                    mobile.value = userAddress.recieverMobile;
                } else {
                    first_name.value = "";
                    last_name.value = "";
                    city_select.value = 0;
                    city_select.disabled = false;
                    city_select.dispatchEvent(new Event("change"));
                    line_one.value = "";
                    line_two.value = "";
                    postal_code.value = "";
                    mobile.value = "";
                }

            });

            let st_tbody = document.getElementById("st-tbody");

            st_tbody.innerHTML = "<h3 class='summary-title'><i class='fas fa-shopping-bag me-2'></i> Your Order</h3>";

            let total = 0;
            let item_count = 0;
            json.cartList.forEach(cart => {
                item_count += cart.qty;
                let item_sub_total = Number(cart.qty) * Number(cart.product.price);
                let table = `<div class="summary-item" id="st-item-tr">
                            <span id="st-product-title">${cart.product.title} (${cart.qty})</span>
                            <span>Rs. <span id="st-product-price">${item_sub_total}.00</span> </span>
                        </div>`;

                st_tbody.innerHTML += table;
                total += item_sub_total;
            });

            //     document.getElementById("st-subtotal").innerHTML = "10";

            let shipping_charges = 0;

            city_select.addEventListener("change", (e) => {
                let cityName = city_select.options[city_select.selectedIndex].innerHTML;

                if (cityName === "Colombo") {
                    shipping_charges = deliveryTypes[0].price;
                } else {
                    // out of Colombo
                    shipping_charges = deliveryTypes[1].price;
                }

                let finalTotal = shipping_charges + total;

                document.getElementById("st-product-shipping-charges").innerHTML = shipping_charges;
                document.getElementById("st-product-total-amount").innerHTML = finalTotal;
            });



            st_tbody.innerHTML += ` <div class="summary-item">
                            <span>Subtotal</span>
                            <span>Rs. <span  id="st-subtotal">${total}</span></span>
                        </div>
                        <div class="summary-item" id="st-order-shipping-tr">
                            <span>Shipping</span>
                            <span>Rs. <span id="st-product-shipping-charges">${shipping_charges}</span></span>
                        </div>

                        <div class="summary-item summary-total">
                            <span>Total</span>
                            <span>Rs. <span id="st-product-total-amount">${total}</span> </span>
                        </div>

                        <button type="button" class="btn-checkout" id="proceedToPayment"  onclick="checkout(event)";>
                            <i class="fas fa-lock me-2"></i> Proceed to Payment
                        </button>

                        <p class="small text-muted mt-3 mb-0">
                            <i class="fas fa-shield-alt me-1"></i> 100% Secure Payment Processing
                        </p>`;

        } else {
            if (json.message === "empty-cart") {
                swal("Error!", json.message, "error").then(() => {
                    window.location = "index.html";
                });

            } else {
                swal("Error", json.message, "error");
            }
        }
    } else {
        if (response.status === 401) {
            swal("Error!", json.message, "error").then(() => {
                window.location = "Sign-In.html";
            });
        } else {
            swal("Error", "Server Error", "info");
        }

    }

}


async function checkout(e) {
    e.preventDefault();

    let checkbox1 = document.getElementById("checkbox1").checked;
    let first_name = document.getElementById("firstName");
    let last_name = document.getElementById("lastName");
    let city_select = document.getElementById("city-select");
    let line_one = document.getElementById("line1");
    let line_two = document.getElementById("line2");
    let postal_code = document.getElementById("postalCode");
    let mobile = document.getElementById("phone");

    let data = {
        isCurrentAddress: checkbox1,
        firstName: first_name.value,
        lastName: last_name.value,
        citySelect: city_select.value,
        lineOne: line_one.value,
        lineTwo: line_two.value,
        postalCode: postal_code.value,
        mobile: mobile.value
    };

    let dataJSON = JSON.stringify(data);

    const response = await fetch(
            "CheckOut",
            {
                method: "POST",
                body: dataJSON,
                headers: {
                    "Content-Type": "application/json"
                }
            }

    );

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            console.log(json);
            payhere.startPayment(json.payhereJson);
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
            text: "Server Error",
            className: "info",
            style: {
                background: "linear-gradient(to right, #ec008c, #fc6767)",
            }
        }).showToast();
    }




}