document.getElementById("registrationForm").addEventListener("submit", function (event) {
    // Prevent form submission
    event.preventDefault();

    // Get form values
    const firstName = document.getElementById("firstName").value.trim();
    const lastName = document.getElementById("lastName").value.trim();
    const password = document.getElementById("password").value.trim();
    const email = document.getElementById("email").value.trim();
    const mobile = document.getElementById("mobile").value.trim();
    const address = document.getElementById("address").value.trim();

    let isValid = true;
    let errorMessage = "";

    // 1. First Name validation
    if (!/^[a-zA-Z]{6,}$/.test(firstName)) {
        isValid = false;
        errorMessage += "First Name must contain only alphabets and be at least 6 characters long.\n";
    }

    // 2. Password validation
    if (password.length < 6) {
        isValid = false;
        errorMessage += "Password must be at least 6 characters long.\n";
    }

    // 3. Email validation
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        isValid = false;
        errorMessage += "E-mail must follow the format (name@domain.com).\n";
    }

    // 4. Mobile validation
    if (!/^\d{10}$/.test(mobile)) {
        isValid = false;
        errorMessage += "Mobile Number must contain exactly 10 digits.\n";
    }

    // 5. Last Name & Address validation
    if (lastName === "") {
        isValid = false;
        errorMessage += "Last Name must not be empty.\n";
    }

    if (address === "") {
        isValid = false;
        errorMessage += "Address must not be empty.\n";
    }

    // Result
    if (isValid) {
        alert("Registration successful!");
    } else {
        alert("Validation Error:\n" + errorMessage);
    }
});