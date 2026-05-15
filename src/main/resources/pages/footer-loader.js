// Footer loader script for JSP website
// Include this script in any HTML page to load the shared footer

function loadFooter() {
    const footerPlaceholder = document.getElementById('footer-placeholder');
    if (!footerPlaceholder) {
        console.error('Footer placeholder not found. Make sure to include <div id="footer-placeholder"></div> in your HTML.');
        return;
    }

    fetch('footer.html')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(data => {
            // Extract the footer element from the loaded HTML
            const parser = new DOMParser();
            const doc = parser.parseFromString(data, 'text/html');
            const footer = doc.querySelector('footer');

            if (footer) {
                // Extract and apply the CSS styles and links
                const style = doc.querySelector('style');
                const links = doc.querySelectorAll('link[rel="stylesheet"]');
                if (style) {
                    // Check if footer styles are already loaded
                    const existingStyles = document.querySelectorAll('style');
                    let footerStylesExist = false;

                    existingStyles.forEach(existingStyle => {
                        if (existingStyle.textContent.includes('/* Footer */')) {
                            footerStylesExist = true;
                        }
                    });

                    if (!footerStylesExist) {
                        document.head.appendChild(style);
                    }
                }
                // Append Font Awesome and other CSS links if not already present
                links.forEach(link => {
                    const href = link.getAttribute('href');
                    const existingLink = document.querySelector(`link[href="${href}"]`);
                    if (!existingLink) {
                        document.head.appendChild(link);
                    }
                });

                // Insert the footer into the placeholder
                footerPlaceholder.appendChild(footer);
            } else {
                throw new Error('Footer element not found in footer.html');
            }
        })
        .catch(error => {
            console.error('Error loading footer:', error);
            // Fallback: show a simple footer
            footerPlaceholder.innerHTML = '<footer style="background: #222; color: #bbb; padding: 20px; text-align: center;"><p>&copy; 2026 Jain Sangh Pune (JSP). All rights reserved.</p></footer>';
        });
}

// Auto-load footer when DOM is ready
document.addEventListener('DOMContentLoaded', loadFooter);
