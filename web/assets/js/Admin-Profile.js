document.addEventListener('DOMContentLoaded', function () {
    const mobileMenuBtn = document.querySelector('.mobile-menu-btn');
    const sidebar = document.querySelector('.sidebar');
    const sidebarOverlay = document.querySelector('.sidebar-overlay');
    const profileForm = document.getElementById('profileForm');
    const passwordForm = document.getElementById('passwordForm');
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

    document.addEventListener("DOMContentLoaded", loadMainData);
// Handle password form submission
    passwordForm.addEventListener('submit', function (e) {
        e.preventDefault();
        const currentPassword = document.getElementById('currentPassword').value;
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        // Validate passwords
        if (newPassword !== confirmPassword) {
            alert('New passwords do not match!');
            return;
        }

        if (newPassword.length < 8) {
            alert('Password must be at least 8 characters long');
            return;
        }

// Here you would typically send this data to your server
        console.log('Password Change:', {
            currentPassword: currentPassword,
            newPassword: newPassword
        });
        // Show success message
        alert('Password changed successfully!');
        // Reset form
        passwordForm.reset();
    });
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
//profile picture
document.addEventListener('DOMContentLoaded', function () {
    const profilePictureUpload = document.getElementById('profilePictureUpload');
    const uploadPictureBtn = document.getElementById('uploadPictureBtn');
    const removePictureBtn = document.getElementById('removePictureBtn');
    const profilePicturePreview = document.getElementById('profilePicturePreview');
    const updatePictureSubmitBtn = document.querySelector('.form-container button[type="submit"]');
    let selectedFile = null;
    // Trigger file input when button is clicked
    uploadPictureBtn.addEventListener('click', function () {
        profilePictureUpload.click();
    });
    // Handle file selection
    profilePictureUpload.addEventListener('change', function (e) {
        if (e.target.files.length > 0) {
            const file = e.target.files[0];
            if (!file.type.match('image.*')) {
                alert('Please select an image file (JPG, PNG)');
                return;
            }

            if (file.size > 5 * 1024 * 1024) {
                alert('Image size should be less than 5MB');
                return;
            }

            const reader = new FileReader();
            reader.onload = function (event) {
                profilePicturePreview.src = event.target.result;
                removePictureBtn.style.display = 'inline-block';
                selectedFile = file;
            };
            reader.readAsDataURL(file);
        }
    });
    // Upload on clicking "Update Profile Picture"
    updatePictureSubmitBtn.addEventListener('click', function () {
        if (!selectedFile) {
            alert('Please select a picture first.');
            return;
        }
        updateProfilePicture(selectedFile);
    });
    // Handle remove picture
    removePictureBtn.addEventListener('click', function () {
        profilePicturePreview.src = 'assets/Resources/profile.png';
        profilePictureUpload.value = '';
        removePictureBtn.style.display = 'none';
        selectedFile = null;
        console.log('Profile picture removed');
    });
});

function LoadData() {
    loadCityData();
    loadProfileData();
    loadMainData();
}
///admin profile
async function loadCityData() {
    const response = await fetch("LoadCityData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            const Select = document.getElementById("city");
            json.cityList.forEach(item => {

                const Option = document.createElement("option");
                Option.value = item.id;
                Option.innerHTML = item.name;
                Select.appendChild(Option);
            }
            );
        } else {
            document.getElementById("message").innerHTML = "City Loading Error";
        }
    } else {
        document.getElementById("message").innerHTML = "City Loading Fail";
    }
}

//profile image - update section
async function updateProfilePicture(file) {

    const formData = new FormData();
    formData.append('profilePicture', file);
    const response = await fetch("UpdateProfilePicture",
            {
                method: "POST",
                body: formData
            });
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const profileName = json.ProfileName;
            alert("Profile picture updated successfully!");
            window.location.reload();
        } else {
            console.log(json);
            alert("Upload failed. Please try again.");
        }
    } else {
        alert("Server error occurred. Try again.");
    }


}

///////////////////////////Profile Data/////////////////////////////////

async function loadProfileData() {
    const response = await fetch("loadUserData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            document.getElementById("AdminprofilePicture").src = json.file;
            document.getElementById("topImg").src = json.file;
            document.getElementById("admin").innerHTML = json.ProfileName;
        } else {
            alert("error");
        }
    }
}

///////////////////////////////////////////////////////////////////////////////

async function saveData(event) {
    event.preventDefault();

    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const addressLine1 = document.getElementById("addressLine1").value;
    const addressLine2 = document.getElementById("addressLine2").value;
    const postalCode = document.getElementById("postalCode").value;
    const city = document.getElementById("city").value;
    const mobile1 = document.getElementById("mobile1").value;
    const mobile2 = document.getElementById("mobile2").value;
    const form = new FormData();
    form.append("fname", firstName);
    form.append("lname", lastName);
    form.append("ad1", addressLine1);
    form.append("ad2", addressLine2);
    form.append("pc", postalCode);
    form.append("c", city);
    form.append("m1", mobile1);
    form.append("m2", mobile2);

    // alert(city);
    const response = await fetch(
            "SaveAdminInfo",
            {
                method: "POST",
                body: form
            }
    );
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json);
            swal("Success!", json.message, "success").then(() => {
                window.location.reload();
            });
        } else {
            swal("Error!", json.message, "error").then(() => {
                window.location.reload();
            });
            console.log(json);
        }
    } else {
        swal("Error!", "Server Error", "error").then(() => {
            window.location.reload();
        });

    }
}


///change Password
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



//loadMainData
async function loadMainData() {

    const response = await fetch("AdminAccount");

    if (response.ok) {

        const json = await response.json();
        console.log(json);
        document.getElementById("firstName").value = json.firstName;
        document.getElementById("lastName").value = json.lastName;
        document.getElementById("currentPassword").value = json.addressList.password;

        if (json.MobileList && json.MobileList.length > 0) {

            const firstMobile = json.MobileList[0];

            const phone1Element = document.getElementById("mobile1");
            const phone2Element = document.getElementById("mobile2");

            if (phone1Element && firstMobile.mobile1) {
                phone1Element.value = firstMobile.mobile1;
            }

            if (phone2Element && firstMobile.mobile2) {
                phone2Element.value = firstMobile.mobile2;
            }
        }

        if (json.addressList && json.addressList.length > 0) {
            const firstAddress = json.addressList[0];
            document.getElementById("addressLine1").value = firstAddress.line1 || "";
            document.getElementById("addressLine2").value = firstAddress.line2 || "";
            document.getElementById("postalCode").value = firstAddress.postalCode || "";

            const citySelect = document.getElementById("city");

            // Clear current options
            citySelect.innerHTML = "";

            // Add selected city as only option
            if (firstAddress.city && firstAddress.city.name) {
                const cityOption = document.createElement("option");
                cityOption.value = firstAddress.city.id || "";
                cityOption.textContent = firstAddress.city.name;
                cityOption.selected = true;
                citySelect.appendChild(cityOption);
            }


            if (postal_Code && firstAddress.postalCode) {
                postal_Code.innerHTML = firstAddress.postalCode;
            }

            if (cityId && firstAddress.city.name) {
                cityId.innerHTML = firstAddress.city.name;
            }

        }

        console.log(json);

    }


}

async function loadMainData() {
    
    const citySelect = document.getElementById("city");
    citySelect.innerHTML = "";

    const response = await fetch("AdminAccount");
    if (response.ok) {
        const json = await response.json();

        document.getElementById("firstName").value = json.firstName || "";
        document.getElementById("lastName").value = json.lastName || "";

        if (json.MobileList?.length > 0) {
            const {mobile1, mobile2} = json.MobileList[0];
            document.getElementById("mobile1").value = mobile1 || "";
            document.getElementById("mobile2").value = mobile2 || "";
        }

        if (json.addressList?.length > 0) {
            const address = json.addressList[0];
            document.getElementById("addressLine1").value = address.line1 || "";
            document.getElementById("addressLine2").value = address.line2 || "";
            document.getElementById("postalCode").value = address.postalCode || "";

            // Select city from loaded list
            if (address.city?.id) {
                const citySelect = document.getElementById("city");
                const cityOption = [...citySelect.options].find(opt => opt.value === String(address.city.id));
                if (cityOption)
                    cityOption.selected = true;
            }
                      
        }
        console.log(json);
        document.getElementById("currentPassword").value = json.addressList[0].user.password;
    }
}
