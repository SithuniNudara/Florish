// Simple tab switching functionality
document.addEventListener('DOMContentLoaded', function () {
    // Initialize the first tab
    document.querySelector('.account-sidebar .nav-link').click();
});

function loadData() {
    getCityData();
    loadMainData();
    MyCart();
    myOrder();
}

////////load cities/////////////////////
async function getCityData() {

    const response = await fetch("CityData");

    if (response.ok) {
        const json = await response.json();
        // console.log(json);
        const citySelect = document.getElementById("cityId");
        json.forEach(city => {
            let option = document.createElement("option");
            option.innerHTML = city.name;
            option.value = city.id;
            citySelect.appendChild(option);
        });

    }

}

/////////////Load Main Data/////////////////////
async function loadMainData() {

    const response = await fetch("MyAccount");

    if (response.ok) {

        const json = await response.json();
        document.getElementById("firstName").value = json.firstName;
        document.getElementById("lastName").value = json.lastName;
        document.getElementById("email").value = json.email;
        document.getElementById("since").innerHTML = `Member since ` + json.since;
        document.getElementById("currentPassword").value = json.password;

        if (json.MobileList && json.MobileList.length > 0) {

            const firstMobile = json.MobileList[0];

            const phone1Element = document.getElementById("phone1");
            const phone2Element = document.getElementById("phone2");

            if (phone1Element && firstMobile.mobile1) {
                phone1Element.value = firstMobile.mobile1;
            }

            if (phone2Element && firstMobile.mobile2) {
                phone2Element.value = firstMobile.mobile2;
            }
        }

        if (json.addressList && json.addressList.length > 0) {

            const firstAddress = json.addressList[0];

            const receiverName = document.getElementById("addName");
            receiverName.innerHTML = json.addressList[0].firstName + " " + json.addressList[0].lastName;

            const line1 = document.getElementById("lineOne");
            const line2 = document.getElementById("lineTwo");

            const postal_Code = document.getElementById("postalCode");

            const cityId = document.getElementById("city");

            const receiverMobile = document.getElementById("receiverMobile");

            if (line1 && firstAddress.line1) {
                line1.innerHTML = firstAddress.line1;
            }

            if (line2 && firstAddress.line2) {
                line2.innerHTML = firstAddress.line2;
            }

            if (postal_Code && firstAddress.postalCode) {
                postal_Code.innerHTML = firstAddress.postalCode;
            }

            if (cityId && firstAddress.city.name) {
                cityId.innerHTML = firstAddress.city.name;
            }

            if (receiverMobile && firstAddress.recieverMobile) {
                receiverMobile.innerHTML = firstAddress.recieverMobile;
            }
        }

        document.getElementById("hello").innerHTML = `Hello, ` + json.firstName + " " + json.lastName;

        document.getElementById("registeredDate").value = json.joined;

        console.log(json);

        if (json.hasOwnProperty("addressList") && json.addressList !== undefined) {
            let email;
            let lineOne;
            let lineTwo;
            let city;
            let postalCode;
            let cityId;
            const addressUL = document.getElementById("addressUL");
            json.addressList.forEach(address => {
                email = address.user.email;
                lineOne = address.lineOne;
                lineTwo = address.lineTwo;
                city = address.city.name;
                postalCode = address.postalCode;
                cityId = address.city.id;
                const line = document.createElement("li");
                line.innerHTML = lineOne + ",<br/>" +
                        lineTwo + ",<br/>" +
                        city + "<br/>" +
                        postalCode;
                addressUL.appendChild(line);
            });
            if (json.hasOwnProperty("MobileList") && json.MobileList !== undefined) {
                let mobile1;
                let mobile2;
                const mobilesUL = document.getElementById("mobiles");
                json.MobileList.forEach(mobile => {
                    mobile1 = mobile.user.mobile1;
                    mobile2 = mobile.user.mobile2;
                    const line = document.createElement("li");
                    line.innerHTML = mobile1 + ",<br/>" +
                            mobile2;
                    mobilesUL.appendChild(line);
                });

                document.getElementById("addName").innerHTML = `Name: ${json.firstName} ${json.lastName}`;
                document.getElementById("addEmail").innerHTML = `Email: ${email}`;
                document.getElementById("contact").innerHTML = `Phone: 011-2215453`;

                document.getElementById("lineOne").value = lineOne;
                document.getElementById("lineTwo").value = lineTwo;
                document.getElementById("postalCode").value = postalCode;
                document.getElementById("city").value = parseInt(cityId);

            }


        }

    }


}


////////////////change password////////////////
async function changePassword(e) {
    e.preventDefault();
    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    const data = {
        current: currentPassword,
        new : newPassword,
        confirm: confirmPassword
    };

    const jsonObject = JSON.stringify(data);

    const response = await fetch(
            "ChangePw",
            {
                method: "POST",
                body: jsonObject,
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

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
        swal("Error!", "Server Error", "info").then(() => {
            window.location.reload();
        });
    }

}


///////////////Save Main Details//////////////////////

async function updateUserProfile(event) {
    event.preventDefault();

    const firstname = document.getElementById("firstName").value;
    const lastname = document.getElementById("lastName").value;
    const phone1 = document.getElementById("phone1").value;
    const phone2 = document.getElementById("phone2").value;

    const data = {
        firstname: firstname,
        lastname: lastname,
        phone1: phone1,
        phone2: phone2

    };

    const dataJson = JSON.stringify(data);
    console.log(dataJson);

    const response = await fetch(
            "SaveUserMainData", {
                method: "POST",
                body: dataJson,
                headers: {
                    "Content-Type": "application/json"
                }
            }

    );

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
        console.log("Server Error: " + response.status);
    }

}

////////////////////////////Address///////////////////////////////////

async function saveAddressBook(event) {
    event.preventDefault();

 //   const reciverName = document.getElementById("receiverName").value;
    const receiverName = document.getElementById("receiverName").value.trim();
    const [firstName, ...rest] = receiverName.split(" ");
    const lastName = rest.join(" ");
    
    const addressLine1 = document.getElementById("line_1").value;
    const addressLine2 = document.getElementById("line_2").value;
    const cityId = document.getElementById("cityId").value;
    const postalCode = document.getElementById("postal_code").value;
    const mobile = document.getElementById("mobilenum").value;



    const dataObject = {
        fname:firstName,
        lname:lastName,
        line1: addressLine1,
        line2: addressLine2,
        cid: cityId,
        postalCode: postalCode,
        mobile: mobile
    };

    // console.log(dataObject);

    const JsonObject = JSON.stringify(dataObject);

    const response = await fetch(
            "UserAddressSave",
            {
                method: "POST",
                body: JsonObject,
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

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
        swal("Error!", "Server Error", "info").then(() => {
            window.location.reload();
        });
    }
}

/////////////Update Address/////////////////////////////////////

function edit() {
    const reciverName = document.getElementById("addName").innerHTML;
    const addressLine1 = document.getElementById("lineOne").innerHTML;
    const addressLine2 = document.getElementById("lineTwo").innerHTML;
    const city = document.getElementById("city").innerHTML;
    const postalCode = document.getElementById("postalCode").innerHTML;
    const mobile = document.getElementById("receiverMobile").innerHTML;
    //city = Matara

    document.getElementById("receiverName").value = reciverName;
    document.getElementById("line_1").value = addressLine1;
    document.getElementById("line_2").value = addressLine2;
    document.getElementById("postal_code").value = postalCode;
    document.getElementById("mobilenum").value = mobile;

    const citySelect = document.getElementById("cityId");
    const options = citySelect.options;
    for (let i = 0; i < options.length; i++) {
        if (options[i].text.trim().toLowerCase() === city.toLowerCase()) {
            citySelect.selectedIndex = i;
            break;
        }
    }
}

async function deleteAddress() {
    const response = await fetch("deleteUserAddress");

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
        swal("Error!", "Server Error", "info").then(() => {
            window.location.reload();
        });
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

//async function myOrder() {
//    const response = await fetch("LoadUserOrders");
//
//    if (response.ok) {
//        const json = await response.json();
//
//        if (json.status) {
//            let orderItems = json.OrderItemList;
//            let container = document.getElementById("container");
//            container.innerHTML = "";
//
//
//            orderItems.forEach(item => {
//                let order = item.orders;
//                const badgeHTML = getStatusBadge(item.orderStatus.value);
//
//                let date = new Date(order.createdAt);
//                const formattedDate = date.toLocaleDateString('en-US', {
//                    year: 'numeric',
//                    month: 'short',
//                    day: 'numeric'
//                });
//
//                let product = item.product;
//                let total = (product.price) * item.qty;
//
//                const card = `
//
//
//        <tr>
//            <!-- Order Info Column (50% width) -->
//            <td class="w-50 p-3">
//                <h6 class="mb-1">Order #${order.id}</h6>
//                <p class="small text-muted mb-2">Placed on ${formattedDate}</p>
//                ${badgeHTML}
//            </td>
//            
//            <!-- Items & Price Column (25% width, center aligned) -->
//            <td class="w-25 p-3 text-center align-middle">
//                <p class="mb-1">${item.qty} Item${item.qty > 1 ? 's' : ''}</p>
//                <p class="text-florish fw-bold">Rs.${total.toLocaleString()}.00</p>
//            </td>
//                <td>
//<button class="btn btn-sm btn-outline-primary">Add Rating</button>
//   </td>
//            
//        </tr>
//
//                
//`;
//                container.innerHTML += card;
//
//            });
//
//
//
//            console.log(json);
//        } else {
//            console.log(json.message);
//        }
//    }
//}

function getStatusBadge(status) {
    switch (status.toLowerCase()) {
        case 'delivered':
            return `<span class="badge bg-success">Delivered</span>`;
        case 'shipped':
            return `<span class="badge bg-primary">Shipped</span>`;
        case 'pending':
            return `<span class="badge bg-warning text-dark">Pending</span>`;
        default:
            return `<span class="badge bg-secondary">${status}</span>`;
    }
}


function downloadPDF() {
    const element = document.getElementById('pdf-content');
    const opt = {
        margin: 0.5,
        filename: 'order-report.pdf',
        image: {type: 'jpeg', quality: 0.98},
        html2canvas: {scale: 2, scrollY: 0},
        jsPDF: {unit: 'in', format: 'a4', orientation: 'portrait'},
        pagebreak: {mode: ['avoid-all', 'css']}
    };
    html2pdf().set(opt).from(element).save();
}


async function myOrder() {
    const response = await fetch("LoadUserOrders");

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            let orderItems = json.OrderItemList;
            let container = document.getElementById("container");
            container.innerHTML = "";

            orderItems.forEach(item => {
                let order = item.orders;
                const badgeHTML = getStatusBadge(item.orderStatus.value);

                let date = new Date(order.createdAt);
                const formattedDate = date.toLocaleDateString('en-US', {
                    year: 'numeric',
                    month: 'short',
                    day: 'numeric'
                });

                let product = item.product;

                let total = (product.price) * item.qty;

                const card = `
            <tr>
            <td class="w-50 p-3">
                <h6 class="mb-1">Order #${order.id}</h6>
                <p class="small text-muted mb-2">Placed on ${formattedDate}</p>
                ${badgeHTML}
            </td>
            
            <!-- Items & Price Column (25% width, center aligned) -->
            <td class="w-25 p-3 text-center align-middle">
                <p class="mb-1">${item.qty} Item${item.qty > 1 ? 's' : ''}</p>
                <p class="text-florish fw-bold">Rs.${total.toLocaleString()}.00</p>
            </td>
            <td>
                <button class="btn btn-sm btn-outline-primary rate-btn"
                    data-product-id="${product.id}"
                    data-order-id="${order.id}" //new Added
                    data-order-index="${orderItems.indexOf(item)}">
                    Add Rating
                </button>
            </td>
        </tr>
                `;
                container.innerHTML += card;
            });

            document.querySelectorAll('.rate-btn').forEach(btn => {
                btn.addEventListener('click', (e) => {
                    const productId = e.target.getAttribute('data-product-id');
                    const index = e.target.getAttribute('data-order-index');


                    const item = orderItems[index];
                    if (item && item.product && item.product.id == productId) {
                        openRatingPopup(item);
                    } else {
                        console.error("Invalid product/item data");
                    }
                });
            });

            console.log(json);
        } else {
            console.log(json.message);
        }
    }
}

function openRatingPopup(item) {
    const popup = document.getElementById('ratingPopup');
    const product = item.product;

    const maxLength = 100; // 5 Lines wage
    const description = product.description || 'No description available';

    const truncatedDesc = description.length > maxLength
            ? `${description.substring(0, maxLength)}...`
            : description;

    document.getElementById('popupProductName').textContent = product.title;
    document.getElementById('popupProductCategory').textContent = product.category.value;
    document.getElementById('popupProductDescription').textContent = truncatedDesc;
    document.getElementById('popupProductImage').src = `product-images/${product.id}/image1.png`;
    document.getElementById('selectedRating').textContent = '0';
    document.getElementById('ratingComment').value = '';

    // Reset stars
    document.querySelectorAll('.star').forEach(star => {
        star.classList.remove('active');
        star.style.color = '#ddd';
    });

    // Set product ID for submission
    popup.setAttribute('data-product-id', product.id);
    popup.setAttribute('data-order-id', item.orders.id);

    // Show popup
    popup.style.display = 'flex';
}

document.addEventListener('DOMContentLoaded', function () {
    const ratingPopup = document.getElementById('ratingPopup');
    const closeBtn = document.getElementById('closeRatingPopup');
    const stars = document.querySelectorAll('.star');
    const selectedRating = document.getElementById('selectedRating');
    const submitBtn = document.getElementById('submitRating');

    let currentRating = 0;

    // Close popup
    closeBtn.addEventListener('click', () => {
        ratingPopup.style.display = 'none';
    });

    // Star rating interaction
    stars.forEach(star => {
        star.addEventListener('click', () => {
            const value = parseInt(star.getAttribute('data-value'));
            currentRating = value;
            selectedRating.textContent = value;

            stars.forEach(s => {
                s.classList.toggle('active', parseInt(s.getAttribute('data-value')) <= value);
            });
        });

        star.addEventListener('mouseover', () => {
            const value = parseInt(star.getAttribute('data-value'));

            stars.forEach(s => {
                const sValue = parseInt(s.getAttribute('data-value'));
                s.style.color = sValue <= value ? 'var(--warning-color)' : '#ddd';
            });
        });

        star.addEventListener('mouseout', () => {
            stars.forEach(s => {
                const sValue = parseInt(s.getAttribute('data-value'));
                s.style.color = sValue <= currentRating ? 'var(--warning-color)' : '#ddd';
            });
        });
    });

    // Submit rating
    submitBtn.addEventListener('click', async () => {
        if (currentRating === 0) {
            alert('Please select a rating');
            return;
        }

        const comment = document.getElementById('ratingComment').value;
        const productId = ratingPopup.getAttribute('data-product-id');
        const orderId = ratingPopup.getAttribute('data-order-id');
        try {
            const response = await fetch("SubmitRating", {
                method: "POST",
                headers: {
                    'Content-Type': "application/json",
                },
                body: JSON.stringify({
                    productId: productId,
                    orderId: orderId,
                    rating: currentRating,
                    comment: comment
                })
            });

            if (response.ok) {
                const json = await response.json();
                if (json.status) {
                    swal("Success!", json.message, "success").then(() => {
                        ratingPopup.style.display = 'none';
                    });
                } else {
                    swal("Error!", json.message, "error").then(() => {
                        window.location.reload();
                    });
                }
            } else {
                throw new Error('Network response was not ok');
            }
        } catch (error) {
            console.error('Error submitting rating:', error);
            swal("Error!", "Failed to submit rating. Please try again.", "error").then(() => {
                window.location.reload();
            });
        }
    });
});


