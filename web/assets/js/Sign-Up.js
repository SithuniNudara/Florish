document.addEventListener('DOMContentLoaded', function () {


    const signupForm = document.getElementById('signupForm');
    const verificationModal = document.getElementById('verificationModal');
    const verifyBtn = document.getElementById('verifyBtn');
    const resendBtn = document.getElementById('resendBtn');
    const codeInputs = document.querySelectorAll('.code-input');

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

    // Auto-focus between code inputs
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

    // Handle signup form submission
    signupForm.addEventListener('submit', function (e) {
        e.preventDefault();

        const firstName = document.getElementById('firstName').value;
        const lastName = document.getElementById('lastName').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        // Here you would typically send data to your backend
        console.log('Signup data:', {firstName, lastName, email, password});

        // Show verification modal
        verificationModal.style.display = 'flex';
    });

    // Verify code
    verifyBtn.addEventListener('click', function () {
        let code = '';
        codeInputs.forEach(input => {
            code += input.value;
        });

        if (code.length === 6) {
            verify();
        } else {
            alert('Please enter a valid 6-digit code.');
        }
    });

// Resend code
    resendBtn.addEventListener('click', function () {
        ResendCode();
    });

    // Close modal when clicking outside
    window.addEventListener('click', function (e) {
        if (e.target === verificationModal) {
            verificationModal.style.display = 'none';
        }
    });
});

/////////////////////Sign Up Process/////////////////////////////////////
async function signUp(event) {

    event.preventDefault();

    const firstname = document.getElementById("firstName").value;
    const lastname = document.getElementById("lastName").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;


    const userData = {
        firstName: firstname,
        lastName: lastname,
        email: email,
        password: password
    };

    const UserJSON = JSON.stringify(userData);

    const response = await fetch(
            "SignUp",
            {
                method: "POST",
                body: UserJSON,
                headers: {
                    "Content-type": "application/json"
                }
            }
    );


    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            swal("Success!", json.message, "success").then(() => {
                document.getElementById("verificationModal").classList.add("active");
            });
        } else {
            swal("Error!", json.message, "error");
        }
    } else {
        swal("Server Error!", json.message, "error").then(() => {
            window.location.reload();
        });
    }
}

/////////////Verify Account///////////////////////////

function closeModal() {
    document.getElementById("verificationModal").classList.remove("active");
}

// Handle code input
codeInputs.forEach((input, index) => {
    input.addEventListener('input', function () {
        if (this.value.length === 1 && index < codeInputs.length - 1) {
            codeInputs[index + 1].focus();
        }
    });

    input.addEventListener('keydown', function (e) {
        if (e.key === 'Backspace' && this.value.length === 0 && index > 0) {
            codeInputs[index - 1].focus();
        }
    });
});

function getVerificationCode() {
    const inputs = document.querySelectorAll("#verification-code input");
    let code = "";
    inputs.forEach(input => {
        code += input.value;
    });
    return code;
}

async function verify() {

    const verificationCode = getVerificationCode();

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
                closeModal();
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

