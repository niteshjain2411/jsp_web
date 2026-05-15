// News loader script for JSP website
// Include this script in any HTML page to load the shared news

function loadNews() {
    const newsPlaceholder = document.getElementById('news-placeholder');
    if (!newsPlaceholder) {
        console.error('News placeholder not found. Make sure to include <div id="news-placeholder"></div> in your HTML.');
        return;
    }

    fetch('news.html')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(data => {
            // Extract the news element from the loaded HTML
            const parser = new DOMParser();
            const doc = parser.parseFromString(data, 'text/html');
            const news = doc.querySelector('news');

            if (news) {
                // Extract and apply the CSS styles
                const style = doc.querySelector('style');
                if (style) {
                    // Check if styles are already loaded
                    const existingStyles = document.querySelectorAll('style');
                    let newsStylesExists = false;

                    existingStyles.forEach(existingStyle => {
                        if (existingStyle.textContent.includes('/* News */')) {
                            newsStylesExists = true;
                        }
                    });

                    if (!newsStylesExists) {
                        document.head.appendChild(style);
                    }
                }

                // Insert the news into the placeholder
                newsPlaceholder.appendChild(news);
            } else {
                throw new Error('News element not found in news.html');
            }
        })
        .catch(error => {
            console.error('Error loading News:', error);
            // Fallback: show a simple news
            newsPlaceholder.innerHTML = '<news style="background: #2a7d8c; color: white; padding: 1rem; text-align: center;"><h1>News</h1></news>';
        });
}

// Auto-load news when DOM is ready
document.addEventListener('DOMContentLoaded', loadNews);
