function loadData() {
    loadProfileData();
    loadPageData();
}
//
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
    const response = await fetch("loadUsersPageData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json);
            document.getElementById("totalUsers").innerHTML = json.total;
            document.getElementById("activeUsers").innerHTML = json.Active;
            document.getElementById("blockedUsers").innerHTML = json.Blocked;
            renderCharts(json);
            let container = document.getElementById("container");
            container.innerHTML = "";


            json.userList.forEach(user => {
                let status = "";
                let badge = "";
                let check = "";
                if (user.userStatus.name === "Active") {
                    status = "Active";
                    badge = "success";
                    check = "checked"
                } else {
                    status = "Block";
                    badge = "danger";
                }
                let userTable = `
<tr>
                                            <td>
                                                ${user.firstName + " " + user.lastName}
                                            </td>
                                            <td>${user.email}</td>
                                            <td>${user.createdAt}</td>
                                            <td><span class="badge bg-${badge}">${status}</span></td>
                                            <td>
                                                <label class="status-toggle">
                                                    <input type="checkbox" ${check} onchange="ChangeUserStatus(${user.id}, this)">
                                                    <span class="slider"></span>
                                                </label>
                                            </td>
                                        </tr>

`;

                container.innerHTML += userTable;
            });
        } else {
            console.log(json);
        }
    }
}


async function ChangeUserStatus(userId, checkboxElement) {

    let newStatus = checkboxElement.checked ? "active" : "block";

    console.log("Changing status of user", userId, "to", newStatus);

    const response = await fetch("chageUserStatus?id=" + userId);

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


function renderCharts(data) {
    const {total, Active, Blocked} = data;

    const pieCtx = document.getElementById('statusPieChart').getContext('2d');
    new Chart(pieCtx, {
        type: 'pie',
        data: {
            labels: ['Active', 'Blocked'],
            datasets: [{
                    data: [Active, Blocked],
                    backgroundColor: ['#4CAF50', '#F44336']
                }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {position: 'bottom'}
            }
        }
    });

    const barCtx = document.getElementById('userBarChart').getContext('2d');
    new Chart(barCtx, {
        type: 'bar',
        data: {
            labels: ['Total', 'Active', 'Blocked'],
            datasets: [{
                    label: 'User Count',
                    data: [total, Active, Blocked],
                    backgroundColor: ['#2196F3', '#4CAF50', '#F44336']
                }]
        },
        options: {
            responsive: true,
            scales: {
                y: {beginAtZero: true}
            }
        }
    });
}

async function searchUser() {

    const keyword = document.getElementById("userSearch").value;

    const response = await fetch("serchUser?keyword=" + keyword);

    if (response.ok) {

        const json = await response.json();

        if (json.status) {
            console.log(json);
            let container = document.getElementById("container");
            container.innerHTML = "";


            json.userList.forEach(user => {
                let status = "";
                let badge = "";
                let check = "";
                if (user.userStatus.name === "Active") {
                    status = "Active";
                    badge = "success";
                    check = "checked"
                } else {
                    status = "Block";
                    badge = "danger";
                }
                let userTable = `
<tr>
                                            <td>
                                                ${user.firstName + " " + user.lastName}
                                            </td>
                                            <td>${user.email}</td>
                                            <td>${user.createdAt}</td>
                                            <td><span class="badge bg-${badge}">${status}</span></td>
                                            <td>
                                                <label class="status-toggle">
                                                    <input type="checkbox" ${check} onchange="ChangeUserStatus(${user.id}, this)">
                                                    <span class="slider"></span>
                                                </label>
                                            </td>
                                        </tr>

`;

                container.innerHTML += userTable;
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
