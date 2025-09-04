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

    const response = await fetch("loadPaymentIfo");

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            document.getElementById("Revenue").innerHTML = "Rs. " + json.allTotal + ".00";
            console.log(json);
            charts(json);
            let container = document.getElementById("container");
            container.innerHTML = "";

            json.data.forEach(order => {
                // If this order has items
                if (Array.isArray(order.items) && order.items.length > 0) {
                    order.items.forEach(item => {
                        let dataTable = `
                <tr>
                    <td>#ORDER${order.order_id}</td>
                    <td>${order.order_date}</td>
                    <td>${item.product_name}</td>
                    <td>${item.quantity}</td>
                    <td>${item.unit_price}</td>
                    <td>${item.line_total}</td>
                    <td>${order.customer_name}</td>
                </tr>
            `;
                        container.innerHTML += dataTable;
                    });
                }
            });
        } else {
            console.log(json.message);
        }
    } else {
        alert(response.status);
    }
}

function charts(json) {
    const productTotals = {};
    const orderTotals = {};

    json.data.forEach(order => {
        let orderTotal = 0;
        order.items.forEach(item => {
            const name = item.product_name;
            const total = item.line_total;

            productTotals[name] = (productTotals[name] || 0) + total;
            orderTotal += total;
        });
        orderTotals[`Order #${order.order_id}`] = orderTotal;
    });

    const pieCtx = document.getElementById("pieChart").getContext("2d");
    new Chart(pieCtx, {
        type: 'pie',
        data: {
         //   labels: Object.keys(productTotals),
            datasets: [{
                    label: "Revenue by Product",
                    data: Object.values(productTotals),
                    backgroundColor: [
                        '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'
                    ]
                }]
        }
    });

    const barCtx = document.getElementById("barChart").getContext("2d");
    new Chart(barCtx, {
        type: 'bar',
        data: {
            labels: Object.keys(orderTotals),
            datasets: [{
                    label: "Order Totals",
                    data: Object.values(orderTotals),
                    backgroundColor: '#36A2EB'
                }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
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

