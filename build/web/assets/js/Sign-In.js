document.addEventListener('DOMContentLoaded', function () {
    // Product Slider (5-second transitions)
    const slides = document.querySelectorAll('.slide');
    let currentSlide = 0;

    function showSlide(index) {
        slides.forEach(slide => slide.classList.remove('active'));
        slides[index].classList.add('active');
    }

    function nextSlide() {
        currentSlide = (currentSlide + 1) % slides.length;
        showSlide(currentSlide);
    }

    setInterval(nextSlide, 5000);

    // Login Form Handling
    const loginForm = document.getElementById('loginForm');
    const forgotPassword = document.getElementById('forgotPassword');
    const verificationModal = document.getElementById('verificationModal');
    const verificationCodeModal = document.getElementById('verificationCodeModal');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const showCodeModalBtn = document.getElementById('showCodeModalBtn');
    const submitCodeBtn = document.getElementById('submitCodeBtn');
    const resendCodeBtn = document.getElementById('resendCodeBtn');
    const codeInputs = document.querySelectorAll('.verification-code-input');

    // Auto-focus and move between verification code inputs
    codeInputs.forEach((input, index) => {
        input.addEventListener('input', (e) => {
            if (e.target.value.length === 1 && index < codeInputs.length - 1) {
                codeInputs[index + 1].focus();
            }
        });

        input.addEventListener('keydown', (e) => {
            if (e.key === 'Backspace' && index > 0 && e.target.value === '') {
                codeInputs[index - 1].focus();
            }
        });
    });

    // Close first modal
    closeModalBtn.addEventListener('click', function () {
        verificationModal.style.display = 'none';
    });

// Show verification code modal
    showCodeModalBtn.addEventListener('click', function () {
        verificationModal.style.display = 'none';
        verificationCodeModal.style.display = 'flex';
    });

    // Submit verification code
    submitCodeBtn.addEventListener('click', function () {
        let code = '';
        codeInputs.forEach(input => {
            code += input.value;
        });

        if (code.length === 6) {
            verify(code);
            verificationCodeModal.style.display = 'none';

        } else {
            alert('Please enter a valid 6-digit code.');
        }
    });

    forgotPassword.addEventListener('click', function () {
        window.location = "Rest-Password.html";
    });

// Resend verification code
    resendCodeBtn.addEventListener('click', function () {
        ResendCode();
    });

    // Close modals when clicking outside
    window.addEventListener('click', function (e) {
        if (e.target === verificationModal) {
            verificationModal.style.display = 'none';
        }
        if (e.target === verificationCodeModal) {
            verificationCodeModal.style.display = 'none';
        }
    });
});

/////////////////Login////////////////////////////
const verificationModal = document.getElementById("verificationModal");
async function SignIn(event) {

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const LoginData = {
        Email: email,
        Password: password
    };

    const loginJson = JSON.stringify(LoginData);

    const response = await fetch(
            "SignIn",
            {
                method: "POST",
                body: loginJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            if (json.message === "Not Verified") {
                //  alert("not verifed!");
                verificationModal.style.display = 'flex';
            } else {
                if (json.type === "Admin Login!") {
                    window.location = "Admin-Dashboard.html";
                } else {
                    window.location = "index.html";
                }

            }
        } else {
            swal("Error!", json.message, "error").then(() => {
                closeModal();
                window.location.reload();
            });
        }
    }
}

async function verify(vcode) {

    const verificationCode = vcode;

    // alert(verificationCode);

    const code = {
        code: verificationCode
    };

    const JsonCode = JSON.stringify(code);

    const response = await fetch(
            "VerifyAccount",
            {
                method: "POST",
                body: JsonCode,

                header: {
                    "Content-Type": "application/json"
                }

            }
    );


    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            swal("Success!", json.message, "success").then(() => {
                //     closeModal();
                window.location = "index.html";
            });
        } else {
            swal("Error!", json.message, "error").then(() => {
                closeModal();
                window.location.reload();
            });
        }
    } else {
        swal("Server error: " + response.status, "error");
    }

}


/////////////////Resend Code/////////////////////////////

async function ResendCode() {

    const email = document.getElementById("email").value;

    const response = await fetch("ResendCode", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({email})
    });

    if (response.ok) {
        const json = await response.json();
        alert(json.message);
    } else {
        alert("Server error: " + response.status);
    }

}


