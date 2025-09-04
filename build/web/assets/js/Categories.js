$(document).ready(function () {
    // Sample product data (in a real app, this would come from an API)
    const products = {
        all: [
            {id: 1, name: "Hair Shampoo", price: "1,200.00", category: "hair", image: "https://via.placeholder.com/300x200?text=Hair+Shampoo", rating: 4.5},
            {id: 2, name: "Body Lotion", price: "1,500.00", category: "body", image: "https://via.placeholder.com/300x200?text=Body+Lotion", rating: 4},
            {id: 3, name: "Face Cream", price: "2,000.00", category: "face", image: "https://via.placeholder.com/300x200?text=Face+Cream", rating: 5},
            {id: 4, name: "Foot Scrub", price: "800.00", category: "foot", image: "https://via.placeholder.com/300x200?text=Foot+Scrub", rating: 4},
            {id: 5, name: "Hair Conditioner", price: "1,400.00", category: "hair", image: "https://via.placeholder.com/300x200?text=Hair+Conditioner", rating: 4.5},
            {id: 6, name: "Body Wash", price: "1,100.00", category: "body", image: "https://via.placeholder.com/300x200?text=Body+Wash", rating: 4}
        ],
        hair: [
            {id: 1, name: "Hair Shampoo", price: "1,200.00", category: "hair", image: "https://via.placeholder.com/300x200?text=Hair+Shampoo", rating: 4.5},
            {id: 5, name: "Hair Conditioner", price: "1,400.00", category: "hair", image: "https://via.placeholder.com/300x200?text=Hair+Conditioner", rating: 4.5},
            {id: 7, name: "Hair Oil", price: "900.00", category: "hair", image: "https://via.placeholder.com/300x200?text=Hair+Oil", rating: 4}
        ],
        body: [
            {id: 2, name: "Body Lotion", price: "1,500.00", category: "body", image: "https://via.placeholder.com/300x200?text=Body+Lotion", rating: 4},
            {id: 6, name: "Body Wash", price: "1,100.00", category: "body", image: "https://via.placeholder.com/300x200?text=Body+Wash", rating: 4},
            {id: 8, name: "Body Scrub", price: "1,300.00", category: "body", image: "https://via.placeholder.com/300x200?text=Body+Scrub", rating: 4.5}
        ],
        face: [
            {id: 3, name: "Face Cream", price: "2,000.00", category: "face", image: "https://via.placeholder.com/300x200?text=Face+Cream", rating: 5},
            {id: 9, name: "Face Wash", price: "1,200.00", category: "face", image: "https://via.placeholder.com/300x200?text=Face+Wash", rating: 4.5},
            {id: 10, name: "Face Mask", price: "1,500.00", category: "face", image: "https://via.placeholder.com/300x200?text=Face+Mask", rating: 4}
        ],
        foot: [
            {id: 4, name: "Foot Scrub", price: "800.00", category: "foot", image: "https://via.placeholder.com/300x200?text=Foot+Scrub", rating: 4},
            {id: 11, name: "Foot Cream", price: "1,000.00", category: "foot", image: "https://via.placeholder.com/300x200?text=Foot+Cream", rating: 4},
            {id: 12, name: "Foot Soak", price: "750.00", category: "foot", image: "https://via.placeholder.com/300x200?text=Foot+Soak", rating: 4.5}
        ]
    };

    // Category tab click handler
    $('.category-tab').click(function () {
        // Update active tab
        $('.category-tab').removeClass('active');
        $(this).addClass('active');

        // Get selected category
        const category = $(this).data('category');

        // Show loading spinner
        $('#products-container').addClass('d-none');
        $('#loading').removeClass('d-none');

        // Simulate API call delay
        setTimeout(function () {
            loadProducts(category);

            // Hide loading spinner and show products
            $('#loading').addClass('d-none');
            $('#products-container').removeClass('d-none');
        }, 800);
    });

    // Function to load products
    function loadProducts(category) {
        const container = $('#products-container');
        container.empty();

        const categoryProducts = products[category] || products.all;

        categoryProducts.forEach((product, index) => {
            const ratingStars = getRatingStars(product.rating);

            const productCard = `
                        <div class="col-md-4 col-sm-6">
                            <div class="product-card" style="animation-delay: ${0.1 + (index * 0.1)}s">
                                <img src="${product.image}" class="product-img" alt="${product.name}">
                                <div class="product-body">
                                    <h5 class="product-title">${product.name}</h5>
                                    <div class="product-rating">
                                        ${ratingStars}
                                    </div>
                                    <p class="product-price">Rs. ${product.price}</p>
                                    <button class="add-to-cart">Add to Cart</button>
                                </div>
                            </div>
                        </div>
                    `;

            container.append(productCard);
        });
    }

    // Helper function to generate rating stars
    function getRatingStars(rating) {
        let stars = '';
        const fullStars = Math.floor(rating);
        const hasHalfStar = rating % 1 >= 0.5;

        for (let i = 0; i < fullStars; i++) {
            stars += '<i class="fas fa-star"></i>';
        }

        if (hasHalfStar) {
            stars += '<i class="fas fa-star-half-alt"></i>';
        }

        const emptyStars = 5 - Math.ceil(rating);
        for (let i = 0; i < emptyStars; i++) {
            stars += '<i class="far fa-star"></i>';
        }

        return stars;
    }

    // Initial load
    loadProducts('all');
});