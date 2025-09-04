document.addEventListener('DOMContentLoaded', function () {
    // Form steps navigation
    const emailStep = document.getElementById('emailStep');
    const verificationStep = document.getElementById('verificationStep');
    const newPasswordStep = document.getElementById('newPasswordStep');
    const emailDisplay = document.getElementById('emailDisplay');
    const emailInput = document.getElementById('email');
    const sendCodeBtn = document.getElementById('sendCodeBtn');
    const verifyCodeBtn = document.getElementById('verifyCodeBtn');
    const resetPasswordBtn = document.getElementById('resetPasswordBtn');
    const resendCodeLink = document.getElementById('resendCode');
    const passwordStrengthBar = document.getElementById('passwordStrengthBar');
    const passwordMatch = document.getElementById('passwordMatch');
    const toggleNewPassword = document.getElementById('toggleNewPassword');
    const toggleConfirmPassword = document.getElementById('toggleConfirmPassword');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');

    // Password requirements elements
    const lengthReq = document.getElementById('lengthReq');
    const uppercaseReq = document.getElementById('uppercaseReq');
    const numberReq = document.getElementById('numberReq');
    const specialReq = document.getElementById('specialReq');

    // Send verification code
    sendCodeBtn.addEventListener('click', function () {
        const email = emailInput.value.trim();

        if (!email) {
            alert('Please enter your email address');
            return;
        }

        if (!validateEmail(email)) {
            alert('Please enter a valid email address');
            return;
        }

        forgetPassword(email);
        emailDisplay.textContent = email;
        emailStep.style.display = 'none';
        verificationStep.style.display = 'block';

        // Simulate sending code (in real app, this would come from your backend)
        console.log('Verification code sent to:', email);
    });

    // Verify code
    verifyCodeBtn.addEventListener('click', function () {
        const email = emailInput.value.trim();
        const code = document.getElementById('verificationCode').value.trim();

        if (code.length !== 6) {
            alert('Please enter the 6-digit verification code');
            return;
        }

        // In a real app, this would verify the code with your backend
        verificationStep.style.display = 'none';
        newPasswordStep.style.display = 'block';
        verify2(code, email);
    });

    // Resend code
    resendCodeLink.addEventListener('click', function (e) {
        e.preventDefault();

        // In a real app, this would resend the verification code
        console.log('Resending verification code to:', emailInput.value.trim());
        alert('A new verification code has been sent to your email');
    });

    // Toggle password visibility
    toggleNewPassword.addEventListener('click', function () {
        togglePasswordVisibility(newPasswordInput, toggleNewPassword);
    });

    toggleConfirmPassword.addEventListener('click', function () {
        togglePasswordVisibility(confirmPasswordInput, toggleConfirmPassword);
    });

    function togglePasswordVisibility(input, icon) {
        const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
        input.setAttribute('type', type);
        icon.classList.toggle('fa-eye');
        icon.classList.toggle('fa-eye-slash');
    }

    // Password strength checker
    newPasswordInput.addEventListener('input', function () {
        const password = newPasswordInput.value;
        checkPasswordStrength(password);
        checkPasswordMatch();
    });

    confirmPasswordInput.addEventListener('input', checkPasswordMatch);

    function checkPasswordStrength(password) {
        // Reset all requirements
        lengthReq.classList.remove('valid');
        uppercaseReq.classList.remove('valid');
        numberReq.classList.remove('valid');
        specialReq.classList.remove('valid');
        lengthReq.innerHTML = '<i class="far fa-circle"></i> At least 8 characters';
        uppercaseReq.innerHTML = '<i class="far fa-circle"></i> At least 1 uppercase letter';
        numberReq.innerHTML = '<i class="far fa-circle"></i> At least 1 number';
        specialReq.innerHTML = '<i class="far fa-circle"></i> At least 1 special character';

        let strength = 0;

        // Check length
        if (password.length >= 8) {
            strength += 25;
            lengthReq.classList.add('valid');
            lengthReq.innerHTML = '<i class="fas fa-check-circle"></i> At least 8 characters';
        }

        // Check uppercase
        if (/[A-Z]/.test(password)) {
            strength += 25;
            uppercaseReq.classList.add('valid');
            uppercaseReq.innerHTML = '<i class="fas fa-check-circle"></i> At least 1 uppercase letter';
        }

        // Check number
        if (/[0-9]/.test(password)) {
            strength += 25;
            numberReq.classList.add('valid');
            numberReq.innerHTML = '<i class="fas fa-check-circle"></i> At least 1 number';
        }

        // Check special character
        if (/[^A-Za-z0-9]/.test(password)) {
            strength += 25;
            specialReq.classList.add('valid');
            specialReq.innerHTML = '<i class="fas fa-check-circle"></i> At least 1 special character';
        }

        // Update strength bar
        passwordStrengthBar.style.width = strength + '%';

        // Update color based on strength
        if (strength < 50) {
            passwordStrengthBar.style.backgroundColor = 'var(--danger-color)';
        } else if (strength < 75) {
            passwordStrengthBar.style.backgroundColor = 'var(--warning-color)';
        } else {
            passwordStrengthBar.style.backgroundColor = 'var(--success-color)';
        }
    }

    function checkPasswordMatch() {
        const password = newPasswordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        if (confirmPassword && password !== confirmPassword) {
            passwordMatch.style.display = 'block';
            return false;
        } else {
            passwordMatch.style.display = 'none';
            return true;
        }
    }

    // Form submission
    document.getElementById('passwordResetForm').addEventListener('submit', function (e) {
        e.preventDefault();

        const password = newPasswordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        if (!checkPasswordMatch()) {
            return;
        }

        if (password.length < 8) {
            alert('Password must be at least 8 characters long');
            return;
        }

        resetPassword(password, confirmPassword);
    });

    // Helper function to validate email
    function validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }
});


async function forgetPassword(email) {
    const response = await fetch("forgetPasswordProcess?email=" + email, {
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {
        const json = await response.json();

        if (!json.status) {
            swal("Error!", json.message, "error").then(() => {
                window.location.reload();
            });
        }

    } else {
        swal("Error!", "Server Error", "error").then(() => {
            window.location.reload();
        });
    }
}


async function verify2(vcode, email) {
    const code = {
        code: vcode,
        email: email
    };

    const response = await fetch("VerifyCodeForgetPassword", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(code)
    });

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            swal("Success!", json.message, "success");
        } else {
            swal("Error!", json.message, "error").then(() => {
                window.location.reload();
            });
        }
    } else {
        swal("Server error: " + response.status, "error");
    }
}


async function resetPassword(pw, cpw) {

    const sendData = {
        Password: pw,
        ConfirmPassword: cpw
    }


    const response = await fetch("ResetPasswordProcess", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(sendData)
    });

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            swal("Success!", json.message, "success").then(() => {
                window.location = "Sign-In.html"; 
            });
        } else {
            swal("Error!", json.message, "error").then(() => {
                window.location.reload();
            });
        }
    } else {
        swal("Server error: " + response.status, "error");
    }

}


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

