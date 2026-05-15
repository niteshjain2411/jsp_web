// Header loader script for JSP website
// Include this script in any HTML page to load the shared header

function loadHeader() {
    const headerPlaceholder = document.getElementById('header-placeholder');
    if (!headerPlaceholder) {
        console.error('Header placeholder not found. Make sure to include <div id="header-placeholder"></div> in your HTML.');
        return;
    }

    fetch('header.html')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(data => {
            // Extract the header element from the loaded HTML
            const parser = new DOMParser();
            const doc = parser.parseFromString(data, 'text/html');
            const header = doc.querySelector('header');

            if (header) {
                // Extract and apply the CSS styles
                const style = doc.querySelector('style');
                if (style) {
                    // Check if styles are already loaded
                    const existingStyles = document.querySelectorAll('style');
                    let headerStylesExist = false;

                    existingStyles.forEach(existingStyle => {
                        if (existingStyle.textContent.includes('/* Header */')) {
                            headerStylesExist = true;
                        }
                    });

                    if (!headerStylesExist) {
                        document.head.appendChild(style);
                    }
                }

                // Insert the header into the placeholder
                headerPlaceholder.appendChild(header);
            } else {
                throw new Error('Header element not found in header.html');
            }
        })
        .catch(error => {
            console.error('Error loading header:', error);
            // Fallback: show a simple header
            headerPlaceholder.innerHTML = '<header style="background: #2a7d8c; color: white; padding: 1rem; text-align: center;"><h1>Jain Sangh Pune</h1></header>';
        });
}

// Auto-load header when DOM is ready
document.addEventListener('DOMContentLoaded', loadHeader);
