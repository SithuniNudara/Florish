document.addEventListener('DOMContentLoaded', function () {
    const imageUploadContainer = document.getElementById('imageUploadContainer');
    const imageUpload = document.getElementById('imageUpload');
    const imagePreviewContainer = document.getElementById('imagePreviewContainer');
    const productForm = document.getElementById('productForm');
    const resetBtn = document.getElementById('resetBtn');
    const mobileMenuBtn = document.querySelector('.mobile-menu-btn');
    const sidebar = document.querySelector('.sidebar');
    const sidebarOverlay = document.querySelector('.sidebar-overlay');

    let uploadedImages = [];
    const maxImages = 3;

    // Only initialize mobile menu if on mobile
    if (window.innerWidth <= 992) {
        // Toggle sidebar on mobile
        function toggleSidebar() {
            sidebar.classList.toggle('active');
            sidebarOverlay.classList.toggle('active');
            document.body.classList.toggle('sidebar-active');
        }

        mobileMenuBtn.addEventListener('click', toggleSidebar);
        sidebarOverlay.addEventListener('click', toggleSidebar);
    } else {
        // Hide mobile menu button on desktop
        mobileMenuBtn.style.display = 'none';
    }

    // Handle click on upload container
    imageUploadContainer.addEventListener('click', function () {
        imageUpload.click();
    });

    // Handle drag and drop
    imageUploadContainer.addEventListener('dragover', function (e) {
        e.preventDefault();
        this.style.borderColor = '#6c5ce7';
        this.style.backgroundColor = 'rgba(108, 92, 231, 0.05)';
    });

    imageUploadContainer.addEventListener('dragleave', function () {
        this.style.borderColor = '#ddd';
        this.style.backgroundColor = '';
    });

    imageUploadContainer.addEventListener('drop', function (e) {
        e.preventDefault();
        this.style.borderColor = '#ddd';
        this.style.backgroundColor = '';

        if (e.dataTransfer.files.length > 0) {
            handleFiles(e.dataTransfer.files);
        }
    });

    // Handle file selection
    imageUpload.addEventListener('change', function () {
        if (this.files.length > 0) {
            handleFiles(this.files);
        }
    });

    // Handle form submission
    productForm.addEventListener('submit', function (e) {
        e.preventDefault();

        // Validate images
        if (uploadedImages.length === 0) {
            alert('Please upload at least one product image');
            return;
        }

//        // Get form values
//        const productData = {
//            title: document.getElementById('productTitle').value,
//            category: document.getElementById('productCategory').value,
//            column: document.getElementById('productColumn').value,
//            description: document.getElementById('productDescription').value,
//            price: document.getElementById('productPrice').value,
//            quantity: document.getElementById('productQuantity').value,
//            status: document.getElementById('productStatus').value,
//            images: uploadedImages
//        };

        // Here you would typically send this data to your server
//        console.log('Product Data:', productData);

        // Show success message
//        alert('Product registered successfully!');

        // Reset form
        resetForm();
    });

    // Handle reset button
    resetBtn.addEventListener('click', resetForm);

    // Function to handle uploaded files
    function handleFiles(files) {
        const remainingSlots = maxImages - uploadedImages.length;

        if (remainingSlots <= 0) {
            alert(`You can only upload up to ${maxImages} images`);
            return;
        }

        const filesToProcess = Math.min(files.length, remainingSlots);

        for (let i = 0; i < filesToProcess; i++) {
            const file = files[i];

            // Validate file type
            if (!file.type.match('image/jpeg') && !file.type.match('image/png')) {
                alert('Only JPEG and PNG images are allowed');
                continue;
            }

            // Validate file size (5MB max)
            if (file.size > 5 * 1024 * 1024) {
                alert('Image size should be less than 5MB');
                continue;
            }

            const reader = new FileReader();

            reader.onload = function (e) {
                const imageData = {
                    name: file.name,
                    size: file.size,
                    type: file.type,
                    data: e.target.result
                };

                uploadedImages.push(imageData);
                renderImagePreviews();
            };

            reader.readAsDataURL(file);
        }
    }

    // Function to render image previews
    function renderImagePreviews() {
        imagePreviewContainer.innerHTML = '';

        uploadedImages.forEach((image, index) => {
            const previewDiv = document.createElement('div');
            previewDiv.className = 'image-preview';

            const img = document.createElement('img');
            img.src = image.data;
            img.alt = 'Product preview';

            const removeBtn = document.createElement('div');
            removeBtn.className = 'remove-image';
            removeBtn.innerHTML = '<i class="fas fa-times"></i>';
            removeBtn.addEventListener('click', function (e) {
                e.stopPropagation();
                uploadedImages.splice(index, 1);
                renderImagePreviews();
            });

            previewDiv.appendChild(img);
            previewDiv.appendChild(removeBtn);
            imagePreviewContainer.appendChild(previewDiv);
        });
    }

    // Function to reset the form
    function resetForm() {
        productForm.reset();
        uploadedImages = [];
        imagePreviewContainer.innerHTML = '';
    }

    // Handle window resize
    window.addEventListener('resize', function () {
        if (window.innerWidth > 992) {
            // On desktop, ensure sidebar is visible
            sidebar.classList.remove('active');
            sidebarOverlay.classList.remove('active');
            mobileMenuBtn.style.display = 'none';
        } else {
            // On mobile, show menu button
            mobileMenuBtn.style.display = 'block';
        }
    });
});

async function loadComboBoxData() {
    const response = await fetch("LoadProductData");

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            loadSelect("productCategory", json.categoryList, "value");
            loadSelect("productStatus", json.productStatusList, "value");
            loadSelect("productVolume", json.volumeList, "value");
        } else {
            console.log(json.status);
        }
    } else {
        alert("error");
    }
}

function loadSelect(selectId, list, property) {

    const Select = document.getElementById(selectId);

    list.forEach(item => {

        const Option = document.createElement("option");
        Option.value = item.id;
        Option.innerHTML = item[property];
        Select.appendChild(Option);
    });

}



async function saveProduct() {

    document.getElementById('loadingSpinner').style.display = 'flex';

    const title = document.getElementById("productTitle").value;
    const category = document.getElementById("productCategory").value;
    const volume = document.getElementById("productVolume").value;
    const description = document.getElementById("productDescription").value;
    const price = document.getElementById("productPrice").value;
    const qty = document.getElementById("productQuantity").value;
    const status = document.getElementById("productStatus").value;

    const imageInput = document.getElementById("imageUpload");

    const files = imageInput.files;

    const img1 = files[0]; // first image
    const img2 = files[1]; // second image
    const img3 = files[2]; // third image


    const form = new FormData();
    form.append("title", title);
    form.append("category", category);
    form.append("volume", volume);
    form.append("description", description);
    form.append("price", price);
    form.append("qty", qty);
    form.append("status", status);
    form.append("img1", img1);
    form.append("img2", img2);
    form.append("img3", img3);

    try {

        const response = await fetch(
                "SaveProduct",
                {
                    method: "POST",
                    body: form
                }
        );
        console.log(response);
        if (response.ok) {
            const json = await response.json();

            if (json.status) {
                swal("Success!", json.message, "success").then(() => {
                    window.location.reload();
                });
            } else {
                if (json.message === "Please Sign In!") {
                    swal("Error!", json.message, "error").then(() => {
                        window.location = "Sign-In.html";
                    });

                } else {
                    swal("Error!", json.message, "error").then(() => {
                        window.location.reload();
                    });
                }
            }
        } else {
            swal("Error!", json.message, "error").then(() => {
                window.location.reload();
            });
        }
    } catch (error) {
        swal("Error!", "Server Error", "info");
        console.error(error);
    } finally {
        document.getElementById('loadingSpinner').style.display = 'none';
    }

}


function loadData() {
    loadProfileData();
    loadProducts();
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



async function loadProducts() {

    const response = await fetch("LoadRegisteredProducts");

    if (response.ok) {
        const json = await response.json();
        console.log(json);
        if (json.status) {
            const productsList = document.getElementById('productsList');
            const noProductsMessage = document.getElementById('noProductsMessage');

            if (json.productList.length === 0) {
                noProductsMessage.style.display = 'block';
                return;
            } else {
                noProductsMessage.style.display = 'none';
            }

            productsList.innerHTML = '';

            json.productList.forEach(product => {
                const productCard = document.createElement('div');
                productCard.className = 'product-card';
                productCard.innerHTML = `
                        <div class="d-flex justify-content-between">
                            <div class="d-flex">
                                <img src="product-images//${product.id}//image2.png" alt="${product.title}" class="product-image me-3">
                                <div>
                                    <h5>${product.title}</h5>
                                    <p class="text-muted mb-1">${product.category.value} • ${product.volumn.value}</p>
                                    <p class="mb-1"><strong>Rs. ${product.price.toFixed(2)}</strong> • Qty: ${product.qty}</p>
                                    <span class="status-badge ${product.product_status.value === 'In Stock' ? 'status-active' : 'status-inactive'}">
                                        ${product.product_status.value}
                                    </span>
                                </div>
                            </div>
                            <div class="product-actions">
                                <button class="btn btn-sm btn-outline-primary edit-product" data-id="${product.id}">
                                    <i class="fas fa-edit"></i>
                                </button>
                            </div>
                        </div>
                    `;
                productsList.appendChild(productCard);
            });

            // Add event listeners for edit/delete buttons
            document.querySelectorAll('.edit-product').forEach(button => {
                button.addEventListener('click', function () {
                    const productId = this.getAttribute('data-id');
                    editProduct(productId);
                });
            });

            document.querySelectorAll('.delete-product').forEach(button => {
                button.addEventListener('click', function () {
                    const productId = this.getAttribute('data-id');
                    deleteProduct(productId);
                });
            });
        } else {
            console.log(json.message);
        }
    } else {
        console.log("server error")
    }


}

async function editProduct(productId) {

    const response = await fetch("editProduct?id=" + productId);

    if (response.ok) {
        const json = await response.json();
        console.log(json);
        if (json.status) {
            let categoryList = json.categoryList
            let statusList = json.productStatusList
            let volumeList = json.volumeList

            let productList = json.productList[0]

            const editModal = new bootstrap.Modal(document.getElementById('editProductModal'));
            editModal.show();

            // Build status options
            let statusOptions = statusList.map(item =>
                    `<option value="${item.id}">${item.value}</option>`
            ).join('');

            // Build category options
            let categoryOptions = categoryList.map(item =>
                    `<option value="${item.id}">${item.value}</option>`
            ).join('');

            // Build volume options
            let volumeOptions = volumeList.map(item =>
                    `<option value="${item.id}">${item.value}</option>`
            ).join('');


            document.querySelector('#editProductModal .modal-body').innerHTML = `
                <form id="editProductForm">
                    <input type="hidden" id="editProductId" value="${productList.id}">

                    <div class="mb-3">
                        <label for="editProductTitle" class="form-label">Product Title</label>
                        <input type="text" class="form-control" id="editProductTitle" value="${productList.title}">
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="editProductPrice" class="form-label">Price (Rs.)</label>
                            <input type="number" class="form-control" id="editProductPrice" value="${productList.price}">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="editProductQuantity" class="form-label">Quantity</label>
                            <input type="number" class="form-control" id="editProductQuantity" value="${productList.qty}">
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-4">
                            <label for="editProductStatus" class="form-label">Status</label>
                            <select class="form-select" id="editProductStatus">
                            ${statusOptions}
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label for="editProductCategory" class="form-label">Category</label>
                            <select class="form-select" id="editProductCategory">
                           ${categoryOptions}
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label for="editProductVolume" class="form-label">Volume</label>
                            <select class="form-select" id="editProductVolume">
                          ${volumeOptions}
                            </select>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="editProductDescription" class="form-label">Description</label>
                        <textarea id="editProductDescription" class="form-control p-3">${productList.description || ''}</textarea>
                    </div>
                </form>
            `;

            document.getElementById('editProductStatus').value = productList.product_status.id;
            document.getElementById('editProductCategory').value = productList.category.id;
            document.getElementById('editProductVolume').value = productList.volumn.id;

            document.getElementById('saveEditBtn').addEventListener('click', function () {

                saveEditedProduct(event);
                editModal.hide();
                loadProducts();
            });
        } else {
            console.log(json.status);
        }
    } else {
        alert("error");
    }


}

async function saveEditedProduct(e) {

    e.preventDefault();
    const id = document.getElementById("editProductId").value;
    const title = document.getElementById("editProductTitle").value;
    const price = document.getElementById("editProductPrice").value;
    const qty = document.getElementById("editProductQuantity").value;
    const status = document.getElementById("editProductStatus").value;
    const category = document.getElementById("editProductCategory").value;
    const volume = document.getElementById("editProductVolume").value;
    const description = document.getElementById("editProductDescription").value;

    const data = {
        id: id,
        title: title,
        price: price,
        qty: qty,
        status: status,
        category: category,
        volume: volume,
        description: description
    };

    const JSONData = JSON.stringify(data);

    const response = await fetch(
            "UpdateProduct",
            {
                method: "POST",
                body: JSONData,
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
        swal("Info!", "Server Error", "info").then(() => {
            window.location.reload();
        });
    }
}

// Initialize the page
document.addEventListener('DOMContentLoaded', function () {
    loadProducts();

    // Search functionality
    document.getElementById('productSearch').addEventListener('input', function (e) {
        const searchTerm = e.target.value.toLowerCase();
        const productCards = document.querySelectorAll('.product-card');

        productCards.forEach(card => {
            const title = card.querySelector('h5').textContent.toLowerCase();
            if (title.includes(searchTerm)) {
                card.style.display = '';
            } else {
                card.style.display = 'none';
            }
        });
    });
});


async function searchProduct() {
    const keyword = document.getElementById("productSearch").value;
    const response = await fetch("SearchProductItem?keyword=" + keyword);

    if (response.ok) {
        const json = await response.json();
        console.log(json);
        if (json.status) {
            const productsList = document.getElementById('productsList');
            const noProductsMessage = document.getElementById('noProductsMessage');

            if (json.productList.length === 0) {
                noProductsMessage.style.display = 'block';
                return;
            } else {
                noProductsMessage.style.display = 'none';
            }

            productsList.innerHTML = '';

            json.searchList.forEach(product => {
                const productCard = document.createElement('div');
                productCard.className = 'product-card';
                productCard.innerHTML = `
                        <div class="d-flex justify-content-between">
                            <div class="d-flex">
                                <img src="product-images//${product.id}//image2.png" alt="${product.title}" class="product-image me-3">
                                <div>
                                    <h5>${product.title}</h5>
                                    <p class="text-muted mb-1">${product.category.value} • ${product.volumn.value}</p>
                                    <p class="mb-1"><strong>Rs. ${product.price.toFixed(2)}</strong> • Qty: ${product.qty}</p>
                                    <span class="status-badge ${product.product_status.value === 'In Stock' ? 'status-active' : 'status-inactive'}">
                                        ${product.product_status.value}
                                    </span>
                                </div>
                            </div>
                            <div class="product-actions">
                                <button class="btn btn-sm btn-outline-primary edit-product" data-id="${product.id}">
                                    <i class="fas fa-edit"></i>
                                </button>
                            </div>
                        </div>
                    `;
                productsList.appendChild(productCard);
            });

            // Add event listeners for edit/delete buttons
            document.querySelectorAll('.edit-product').forEach(button => {
                button.addEventListener('click', function () {
                    const productId = this.getAttribute('data-id');
                    editProduct(productId);
                });
            });

            document.querySelectorAll('.delete-product').forEach(button => {
                button.addEventListener('click', function () {
                    const productId = this.getAttribute('data-id');
                    deleteProduct(productId);
                });
            });
        } else {
            console.log(json.message);
        }
    } else {
        console.log("server error")
    }


}
