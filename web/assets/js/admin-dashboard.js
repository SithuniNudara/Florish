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



function loadData() {
    loadProfileData();
    loadCharts();
    loadOverallData();

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


function loadCharts() {
    loadUserCharts();
    loadOrderStatusChart();
    loadProductPerformanceChart();
    loadTrafficSourcesChart();
}

async function loadUserCharts() {
    const response = await fetch("loadUsersPageData");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const active = json.Active;
            const blocked = json.Blocked;

            const ctx = document.getElementById('userStatusChart').getContext('2d');
            new Chart(ctx, {
                type: 'bar', // you can change to 'bar' if preferred
                data: {
                    labels: ['Active Users', 'Blocked Users'],
                    datasets: [{
                            label: 'User Status Distribution',
                            data: [active, blocked],
                            backgroundColor: ['#4CAF50', '#F44336'],
                            borderColor: ['#388E3C', '#D32F2F'],
                            borderWidth: 1
                        }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'top'
                        },
                        title: {
                            display: true,
                            text: 'User Status Distribution'
                        }
                    }
                }
            });
        } else {

        }
    } else {

    }
}

async function loadOrderStatusChart() {
    const response = await fetch("LoadOrderDetails");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const pending = json.pending;
            const processing = json.processing;
            const shipped = json.shipped;
            const delivered = json.delivered;

            const ctx = document.getElementById('orderStatusChart').getContext('2d');
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: ['Pending', 'Processing', 'Shipped', 'Delivered'],
                    datasets: [{
                            label: 'Order Status Count',
                            data: [pending, processing, shipped, delivered],
                            backgroundColor: [
                                '#f39c12',
                                '#3498db',
                                '#8e44ad',
                                '#2ecc71'
                            ],
                            borderColor: [
                                '#e67e22',
                                '#2980b9',
                                '#7d3c98',
                                '#27ae60'
                            ],
                            borderWidth: 1
                        }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {display: false},
                        title: {
                            display: true,
                            text: 'Order Status Distribution'
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            title: {
                                display: true,
                                text: 'Number of Items'
                            }
                        }
                    }
                }
            });
        } else {
            console.warn("Server response error:", json.message);
        }
    } else {
        console.error("Failed to fetch order status data.");
    }
}

async function loadProductPerformanceChart() {
    const response = await fetch("LoadProductPerformance");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const labels = json.data.map(item => item.title);
            const data = json.data.map(item => item.qty);

            new Chart(document.getElementById('productPerformanceChart').getContext('2d'), {
                type: 'bar',
                data: {
                    labels,
                    datasets: [{
                            label: 'Qty Sold',
                            data,
                            backgroundColor: '#42a5f5'
                        }]
                },
                options: {
                    responsive: true
                }
            });
        }
    }
}

async function loadTrafficSourcesChart() {
    const response = await fetch("LoadTrafficSources");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const labels = json.data.map(i => i.source); // now matches backend
            const data = json.data.map(i => i.count);     // now matches backend

            new Chart(document.getElementById('trafficSourcesChart').getContext('2d'), {
                type: 'pie',
                data: {
                    labels,
                    datasets: [{
                            label: 'User Types',
                            data,
                            backgroundColor: ['#42a5f5', '#66bb6a']
                        }]
                },
                options: {
                    responsive: true
                }
            });
        }
    }
}

async function loadOverallData()
{
    const response = await fetch("LoadOverallData");

    if (response.ok) {

        const json = await response.json();

        if (json.status) {
            console.log(json);
            loadTable1(json);
            loadTable2(json);
            loadTable3(json);
            loadTable4(json);

            document.getElementById("totalUsers").innerHTML = json.totalUsers;
            document.getElementById("totalProducts").innerHTML = json.totalProducts;
            document.getElementById("totalIncome").innerHTML = "Rs. " + json.totalIncome;
            document.getElementById("totalorders").innerHTML = json.totalorders;
        }

    } else {
        console.log("Internal Server Error");
    }
}


async function loadTable1(json) {

    let container = document.getElementById("cont1");
    container.innerHTML = "";
    let count = 0;

    for (const item of json.orderItemsList) {
        if (count >= 5)
            break;
        let badge = "";
        let value = "";

        if (item.orderStatus.value === "Paid") {
            badge = "primary";
            value = "Paid"
        } else if (item.orderStatus.value === "Processing") {
            badge = "warning";
            value = "Processing"
        } else if (item.orderStatus.value === "Shipped") {
            badge = "success";
            value = "Shipped";

        } else if (item.orderStatus.value === "Delivered") {
            badge = "info";
            value = "Delivered";

        } else if (item.orderStatus.value === "Pending") {
            badge = "danger";
            value = "Pending";
        }
        let tableData = `<tr>
                                            <td>${item.orders.id}</td>
                                            <td>${item.orders.user.firstName}${item.orders.user.lastName}</td>
                                            <td>Rs.${item.product.price}.00</td>
                                            <td><span class="badge bg-${badge}">${value}</span></td>
                                        </tr>`;

        container.innerHTML += tableData;
        count++;
    }

}

async function loadTable2(json) {
    let container2 = document.getElementById("cont2");
    container2.innerHTML = "";
    let count = 0;

    for (const item of json.productList) {
        if (count >= 5)
            break;
        let badge = "";
        let value = "";

        if (item.product_status.value === "In Stock") {
            badge = "success";
            value = "In Stock";
        } else if (item.product_status.value === "Out of Stock") {
            badge = "danger";
            value = "Out of Stock";
        }

        let tableData = `<tr>
            <td>${item.title}</td>
            <td>${item.category.value}</td>
            <td>${item.qty}</td>
            <td><span class="badge bg-${badge}">${value}</span></td>
        </tr>`;

        container2.innerHTML += tableData;
        count++;
    }
}
async function loadTable3(json) {
    let container2 = document.getElementById("cont3");
    container2.innerHTML = "";
    let count = 0;

    for (const item of json.orderItemsList) {
        if (count >= 5)
            break;
        let badge = "";
        let value = "";

        if (item.orderStatus.value === "Paid") {
            badge = "primary";
            value = "Paid"
        } else if (item.orderStatus.value === "Processing") {
            badge = "warning";
            value = "Processing"
        } else if (item.orderStatus.value === "Shipped") {
            badge = "success";
            value = "Shipped";

        } else if (item.orderStatus.value === "Delivered") {
            badge = "info";
            value = "Delivered";

        } else if (item.orderStatus.value === "Pending") {
            badge = "danger";
            value = "Pending";
        }
        let tableData = `<tr>
                                            <td>#ORD-${item.id}</td>
                                            <td>${item.deliveryType_id.name}</td>
                                            <td>${item.deliveryType_id.price}</td>
                                            <td><span class="badge bg-${badge}">${value}</span></td>
                                            <td>${item.orders.createdAt}</td>
                                        </tr>`;

        container2.innerHTML += tableData;
        count++;
    }
}


async function loadTable4(json) {
    let container2 = document.getElementById("cont4");
    container2.innerHTML = "";
    let count = 0;

    for (const item of json.userList) {
        if (count >= 5)
            break;
        let badge = "";
        let value = "";

        if (item.userStatus.name === "Active") {
            badge = "success";
            value = "Active"
        } else if (item.userStatus.name === "Block") {
            badge = "danger";
            value = "Block";
        }
        let tableData = ` <tr>
                                            <td>
                                                ${item.firstName} ${item.lastName}
                                            </td>
                                            <td>${item.email}</td>
                                            <td><span class="badge bg-${badge}">${value}</span></td>
                                        </tr>   `;

        container2.innerHTML += tableData;
        count++;
    }
}


