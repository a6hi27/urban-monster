async function pollOrderStatus() {
    const statusElement = document.getElementById('status');
    const urlParams = new URLSearchParams(window.location.search);
    const orderCreationStatus = urlParams.get('orderCreationStatus');
    const orderCreationStatusDetail = urlParams.get('orderCreationStatusDetail');
    try {
        if (orderCreationStatus === 'CREATED') {
            statusElement.innerHTML = orderCreationStatusDetail;
            window.location.href = "/orders.html";
        }
        else if (orderCreationStatus === 'FAILED') {
            statusElement.innerHTML = orderCreationStatusDetail;
            window.location.href = "/orders.html";
        }
        else {
            statusElement.innerHTML = 'Still processing...';
            setTimeout(pollOrderStatus, 2000); // Poll every 2 seconds
        }
    } catch (error) {
        statusElement.innerHTML = 'Error checking status. Retrying...';
        setTimeout(pollOrderStatus, 3000);
    }
}

// Start polling when page loads
document.addEventListener('DOMContentLoaded', pollOrderStatus);