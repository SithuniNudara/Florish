// Toggle sidebar on mobile
const sidebar = document.querySelector('.sidebar');
const mainContent = document.querySelector('.main-content');

function toggleSidebar() {
    if (window.innerWidth <= 768) {
        sidebar.classList.toggle('active');
        mainContent.classList.toggle('active');
    }
}

// Auto-expand on load for mobile
if (window.innerWidth <= 768) {
    sidebar.classList.add('active');
    mainContent.classList.add('active');
}

// Re-draw charts on window resize
window.addEventListener('resize', function () {
    orderTrendsChart.resize();
    orderStatusChart.resize();
});


function loadData() {
    loadProfileData();
    loadPageData();
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


async function loadPageData() {
    const response = await fetch("LoadOrderDetails");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json);
            document.getElementById("processingCount").innerHTML = json.processing;
            document.getElementById("shippedCount").innerHTML = json.shipped;
            document.getElementById("delivered").innerHTML = json.delivered;
            document.getElementById("shipped").innerHTML = json.shipped;

            displayCharts(json);

            let container = document.getElementById("container");
            container.innerHTML = "";

            json.data.forEach(order => {
                if (Array.isArray(order.items) && order.items.length > 0) {
                    order.items.forEach(item => {

                        let badgeClass = "";
                        let statusLabel = "";
                        let selectedStatus = "";
                        let count = 0;

                        const orderId = parseInt(order.order_id);

                        if (item.status.toLowerCase() === "delivered") {
                            badgeClass = "bg-success";
                            statusLabel = "Delivered";
                            selectedStatus = "4";
                        } else if (item.status.toLowerCase() === "pending") {
                            badgeClass = "bg-warning text-dark";
                            statusLabel = "Pending";
                            selectedStatus = "5";
                        } else if (item.status.toLowerCase() === "processing") {
                            badgeClass = "bg-primary";
                            statusLabel = "Processing";
                            selectedStatus = "2";
                        } else if (item.status.toLowerCase() === "shipped") {
                            badgeClass = "bg-info text-dark";
                            statusLabel = "Shipped";
                            selectedStatus = "3";
                        } else if (item.status.toLowerCase() === "paid") {
                            badgeClass = "bg-secondary";
                            statusLabel = "Paid";
                            selectedStatus = "1";
                        }

                        let row = `
                                <tr>
                                    <td>#ORD-${parseInt(order.order_id)}</td>
                                    <td>${order.customer_name}</td>
                                    <td>${order.order_date}</td>
                                    <td>Rs.${item.unit_price.toFixed(2)}</td>
                                    <td>${item.quantity}</td>
                                    <td>Rs.${item.line_total.toFixed(2)}</td>
                                    <td><span class="badge ${badgeClass}">${statusLabel}</span></td>
                                    <td>
                                        <select class="form-select" onchange="updateStatus(${item.item_id}, this.value)">
                                            <option value="1" ${selectedStatus === "1" ? "selected" : ""}>Paid</option>
                                            <option value="2" ${selectedStatus === "2" ? "selected" : ""}>Processing</option>
                                            <option value="3" ${selectedStatus === "3" ? "selected" : ""}>Shipped</option>
                                            <option value="4" ${selectedStatus === "4" ? "selected" : ""}>Delivered</option>
                                            <option value="5" ${selectedStatus === "5" ? "selected" : ""}>Pending</option>
                                        </select>
                                    </td>
                                    <td>
                                        <button class="btn btn-secondary" onclick="viewReview(${item.item_id});">View Review</button>
                                    </td>
                                </tr>`;
                        container.innerHTML += row;
                        count += 1;
                    });
                }
            });
        }
    } else {
        console.log(json);
    }
}
async function updateStatus(itemId, statusId) {

    try {

        const numericItemId = parseInt(itemId);
        const numericStatusId = parseInt(statusId);

        const response = await fetch(`UpdateOrderStatus?id=${numericItemId}&status=${numericStatusId}`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();

        if (result.status) {
            swal("Success!", result.message, "success").then(() => {
                window.location.reload();
            });
        } else {
            swal("Error!", result.message, "error").then(() => {
                window.location.reload();
            });
        }
    } catch (error) {
        swal("Error!", `Failed to update status: ${error.message}`, "error");
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


function displayCharts(json) {
    // ===== Doughnut Chart =====
    const doughnutCtx = document.getElementById('orderStatusChart').getContext('2d');
    new Chart(doughnutCtx, {
        type: 'doughnut',
        data: {
            datasets: [{
                    label: 'Order Status',
                    data: [json.pending, json.processing, json.shipped, json.delivered],
                    backgroundColor: ['#ffc107', '#007bff', '#17a2b8', '#28a745'],
                    borderWidth: 1
                }]
        },
        options: {
            responsive: true
        }
    });

    // ===== Line Chart =====
    const lineCtx = document.getElementById('orderTrendChart').getContext('2d');
    new Chart(lineCtx, {
        type: 'line',
        data: {
            labels: ['Pending', 'Processing', 'Shipped', 'Delivered'],
            datasets: [{
                    label: 'Order Status Count',
                    data: [json.pending, json.processing, json.shipped, json.delivered],
                    backgroundColor: 'rgba(0,123,255,0.2)',
                    borderColor: 'rgba(0,123,255,1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.3
                }]
        },
        options: {
            responsive: true
        }
    });
}


async function viewReview(itemId) {

    try {
        const popup = document.getElementById('reviewPopup');
        popup.style.display = 'flex';

        document.getElementById('reviewItemId').textContent = itemId;

        const response = await fetch("viewRating?itemId=" + itemId);

        if (response.ok) {
            const json = await response.json();
            console.log(json);

            if (json.status) {
                const review = json.review;

                document.getElementById('noReviewMessage').style.display = 'none';
                document.getElementById('ratingValue').textContent = `${review.rating}/10`;
                document.getElementById('reviewStars').innerHTML = getStarsHTML(review.rating);
                document.getElementById('reviewDate').textContent = formatDate(review.order_date);
                document.getElementById('reviewComment').textContent = review.comment || 'No comment provided';
            } else {
                document.getElementById('noReviewMessage').style.display = 'block';
                document.getElementById('ratingValue').textContent = '';
                document.getElementById('reviewStars').innerHTML = '';
                document.getElementById('reviewDate').textContent = '';
                document.getElementById('reviewComment').textContent = '';
            }
        } else {
            console.log("Server Error!");
        }
    } catch (error) {
        console.log('Error fetching review:', error);
        alert('Failed to load review. Please try again.');
        closeReviewPopup();
    }
}

function closeReviewPopup() {
    document.getElementById('reviewPopup').style.display = 'none';
}


function getStarsHTML(rating) {
    const starCount = Math.round(rating);
    return '★'.repeat(starCount) + '☆'.repeat(10 - starCount);
}


function formatDate(dateString) {
    const options = {year: 'numeric', month: 'short', day: 'numeric'};
    return new Date(dateString).toLocaleDateString('en-US', options);
}

// Close popup when clicking outside
window.addEventListener('click', function (event) {
    const popup = document.getElementById('reviewPopup');
    if (event.target === popup) {
        closeReviewPopup();
    }
});
