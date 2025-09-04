function loadHeader() {
const data = `
    <nav class="navbar navbar-expand-lg navbar-dark bg-florish sticky-top">
      <div class="container">
        <a class="navbar-brand fw-bold text-light" href="index.html">Florish</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav me-auto">
          </ul>
          <div class="d-flex">
            <button class="btn btn-outline-light me-2" onclick="window.location='search.html'">
              <i class="fas fa-search"></i>
            </button>
            <button class="btn btn-outline-light me-2 position-relative" data-bs-toggle="offcanvas" data-bs-target="#offcanvasCart">
              <i class="fas fa-shopping-cart"></i>
            </button>
            <div class="dropdown">
              <button class="btn btn-outline-light dropdown-toggle" type="button" id="dropdownMenuButton" data-bs-toggle="dropdown">
                <i class="fas fa-user-circle me-1"></i> User
              </button>
              <ul class="dropdown-menu dropdown-menu-end">
                <li><a class="dropdown-item" href="My-Account.html"><i class="fas fa-user me-2"></i> My Profile</a></li>
                <li><a class="dropdown-item" href="Cart.html"><i class="fa-solid fa-cart-plus me-2"></i> My Cart</a></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" href="" onclick="signOut();"><i class="fas fa-sign-out-alt me-2"></i> Logout</a></li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </nav>
  `;
        document.querySelector("header").innerHTML = data;
        }

function loadFooter() {
const data = `
    <footer class="bg-dark text-white pt-5 pb-4">
      <div class="container">
        <div class="row">
          <div class="col-md-3 mb-4">
            <h5 class="fw-bold mb-4">Florish</h5>
            <p>Bringing ancient Ayurvedic wisdom to modern wellness with pure, natural products for your complete care.</p>
            <div class="social-icons mt-3">
              <a href="#" class="text-white me-2"><i class="fab fa-facebook-f"></i></a>
              <a href="#" class="text-white me-2"><i class="fab fa-instagram"></i></a>
              <a href="#" class="text-white me-2"><i class="fab fa-twitter"></i></a>
              <a href="#" class="text-white"><i class="fab fa-youtube"></i></a>
            </div>
          </div>
          <div class="col-md-3 mb-4">
            <h5 class="fw-bold mb-4">Quick Links</h5>
            <ul class="list-unstyled">
              <li class="mb-2"><a href="#" class="text-white text-decoration-none">Home</a></li>
              <li class="mb-2"><a href="#" class="text-white text-decoration-none">Shop</a></li>
              <li class="mb-2"><a href="#" class="text-white text-decoration-none">About Us</a></li>
              <li class="mb-2"><a href="#" class="text-white text-decoration-none">Contact</a></li>
              <li class="mb-2"><a href="#" class="text-white text-decoration-none">Blog</a></li>
            </ul>
          </div>
          <div class="col-md-3 mb-4">
            <h5 class="fw-bold mb-4">Categories</h5>
            <ul class="list-unstyled">
              <li class="mb-2"><a href="search.html" class="text-white text-decoration-none">Hair Care</a></li>
              <li class="mb-2"><a href="search.html" class="text-white text-decoration-none">Body Care</a></li>
              <li class="mb-2"><a href="search.html" class="text-white text-decoration-none">Foot Care</a></li>
              <li class="mb-2"><a href="search.html" class="text-white text-decoration-none">Face Care</a></li>
              <li class="mb-2"><a href="search.html" class="text-white text-decoration-none">New Arrivals</a></li>
            </ul>
          </div>
          <div class="col-md-3 mb-4">
            <h5 class="fw-bold mb-4">Contact Us</h5>
            <ul class="list-unstyled">
              <li class="mb-2"><i class="fas fa-map-marker-alt me-2"></i> 123 Ayurveda Street, Gardern Rd, Sri Lanka</li>
              <li class="mb-2"><i class="fas fa-phone me-2"></i> +94 718596525</li>
              <li class="mb-2"><i class="fas fa-envelope me-2"></i> info@florish.com</li>
            </ul>
            <h6 class="fw-bold mt-4">Newsletter</h6>
            <div class="input-group mb-3">
              <input type="text" class="form-control" placeholder="Your Email">
              <button class="btn btn-florish" type="button">Subscribe</button>
            </div>
          </div>
        </div>
        <hr class="my-4">
        <div class="row">
          <div class="col-md-6 text-center text-md-start">
            <p class="small mb-0">&copy; 2025 Florish. All rights reserved.</p>
          </div>
          <div class="col-md-6 text-center text-md-end">
            <p class="small mb-0">
              <a href="#" class="text-white text-decoration-none me-2">Privacy Policy</a>
              <a href="#" class="text-white text-decoration-none me-2">Terms of Service</a>
              <a href="#" class="text-white text-decoration-none">Shipping Policy</a>
            </p>
          </div>
        </div>
      </div>
    </footer>
  `;
        document.querySelector("footer").innerHTML = data;
        }

document.addEventListener("DOMContentLoaded", function () {
loadHeader();
        loadFooter();
        });
        async function signOut() {

        const response = await fetch("SignOut");
                if (response.ok) {
        const json = await response.json();
                if (json.status) {

        window.location = "Sign-In.html";
        } else {

        window.location.reload();
        }
        } else {
        console.log("Log Out Fail");
        }
        }

async function signOut() {

const response = await fetch("SignOut");
        if (response.ok) {
const json = await response.json();
        if (json.status === true) {

window.location = "Sign-In.html";
} else {
Toastify({
text: json.message,
        className: "info",
        style: {
        background: "linear-gradient(to right, #ec008c, #fc6767)",
        }
}).showToast();
        window.location.reload();
}
} else {
Toastify({
text: "Sign out Fail",
        className: "info",
        style: {
        background: "linear-gradient(to right, #ec008c, #fc6767)",
        }
}).showToast();
        console.log("Log Out Fail");
}
}

