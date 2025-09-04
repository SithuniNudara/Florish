async function addVolume() {
    
    const vol = document.getElementById("volume-item").value;

    const response = await fetch("AddVolume?volume=" + vol);

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
        swal("Error!", json.message+" "+ json.status, "error").then(() => {
                window.location.reload();
            });

    }
}

