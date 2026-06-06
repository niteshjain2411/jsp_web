// News loader script for JSP website
// Include this script in any HTML page to load the shared news

function getNewsLoaderPath() {
    // Get the script element that loaded this file
    const scriptTags = document.getElementsByTagName('script');
    let loaderPath = null;

    for (let script of scriptTags) {
        if (script.src && script.src.includes('news-loader.js')) {
            loaderPath = script.src;
            break;
        }
    }

    if (!loaderPath && document.currentScript) {
        loaderPath = document.currentScript.src;
    }

    return loaderPath;
}

function resolveNewsPath() {
    const loaderSrc = getNewsLoaderPath();

    if (!loaderSrc) {
        console.warn('Could not determine loader script location');
        return null;
    }

    // Extract the directory from the loader script path
    const lastSlashIndex = loaderSrc.lastIndexOf('/');
    const loaderDir = loaderSrc.substring(0, lastSlashIndex + 1);

    // Construct the path to news.html
    const newsPath = loaderDir + 'news.html';

    console.debug('News loader path resolved to:', newsPath);
    return newsPath;
}

function loadNews() {
    const newsPlaceholder = document.getElementById('news-placeholder');
    if (!newsPlaceholder) {
        console.error('News placeholder not found. Make sure to include <div id="news-placeholder"></div> in your HTML.');
        return;
    }

    const newsPath = resolveNewsPath();
    if (!newsPath) {
        console.error('Failed to resolve news path');
        newsPlaceholder.innerHTML = '<news style="background: #2a7d8c; color: white; padding: 1rem; text-align: center;"><h1>News</h1></news>';
        return;
    }

    fetch(newsPath)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.text();
        })
                .then(data => {
                    // Parse fetched HTML and extract resources + news element
                    const parser = new DOMParser();
                    const doc = parser.parseFromString(data, 'text/html');

                    // Copy stylesheet links
                    const links = doc.querySelectorAll('link[rel="stylesheet"]');
                    links.forEach(link => {
                        const href = link.getAttribute('href');
                        if (!href) return;
                        if (!document.querySelector(`link[rel="stylesheet"][href="${href}"]`)) {
                            const newLink = document.createElement('link');
                            newLink.rel = 'stylesheet';
                            newLink.href = href;
                            document.head.appendChild(newLink);
                        }
                    });

                    // Copy inline styles
                    const styles = doc.querySelectorAll('style');
                    styles.forEach(style => {
                        const text = style.textContent || '';
                        if (!text.trim()) return;
                        let exists = false;
                        document.querySelectorAll('style').forEach(es => {
                            if ((es.textContent || '').trim() === text.trim()) exists = true;
                        });
                        if (!exists) {
                            const newStyle = document.createElement('style');
                            newStyle.textContent = text;
                            document.head.appendChild(newStyle);
                        }
                    });

                    // Include scripts
                    const scripts = doc.querySelectorAll('script');
                    scripts.forEach(s => {
                        try {
                            if (s.src) {
                                if (!document.querySelector(`script[src="${s.src}"]`)) {
                                    const newScript = document.createElement('script');
                                    newScript.src = s.src;
                                    newScript.async = false;
                                    document.body.appendChild(newScript);
                                }
                            } else if ((s.textContent || '').trim()) {
                                const inline = document.createElement('script');
                                inline.text = s.textContent;
                                document.body.appendChild(inline);
                            }
                        } catch (e) {
                            console.warn('News script injection skipped:', e);
                        }
                    });

                    const news = doc.querySelector('news') || doc.querySelector('.news') || doc.body;
                    if (news) {
                        newsPlaceholder.innerHTML = '';
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
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', loadNews);
} else {
    // DOM is already loaded, call immediately
    loadNews();
}
