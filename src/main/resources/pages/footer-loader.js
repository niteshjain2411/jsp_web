// Footer loader script for JSP website
// Include this script in any HTML page to load the shared footer

function getFooterLoaderPath() {
    // Get the script element that loaded this file
    const scriptTags = document.getElementsByTagName('script');
    let loaderPath = null;

    for (let script of scriptTags) {
        if (script.src && script.src.includes('footer-loader.js')) {
            loaderPath = script.src;
            break;
        }
    }

    if (!loaderPath && document.currentScript) {
        loaderPath = document.currentScript.src;
    }

    return loaderPath;
}

function resolveFooterPath() {
    const loaderSrc = getFooterLoaderPath();

    if (!loaderSrc) {
        console.warn('Could not determine loader script location');
        return null;
    }

    // Extract the directory from the loader script path
    const lastSlashIndex = loaderSrc.lastIndexOf('/');
    const loaderDir = loaderSrc.substring(0, lastSlashIndex + 1);

    // Construct the path to footer.html
    const footerPath = loaderDir + 'footer.html';

    console.debug('Footer loader path resolved to:', footerPath);
    return footerPath;
}

function loadFooter() {
    const footerPlaceholder = document.getElementById('footer-placeholder');
    if (!footerPlaceholder) {
        console.error('Footer placeholder not found. Make sure to include <div id="footer-placeholder"></div> in your HTML.');
        return;
    }

    const footerPath = resolveFooterPath();
    if (!footerPath) {
        console.error('Failed to resolve footer path');
        footerPlaceholder.innerHTML = '<footer style="background: #222; color: #bbb; padding: 20px; text-align: center;"><p>&copy; 2026 Jain Sangh Pune (JSP). All rights reserved.</p></footer>';
        return;
    }

    fetch(footerPath)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.text();
        })
                .then(data => {
                    // Parse fetched HTML and extract resources + footer element
                    const parser = new DOMParser();
                    const doc = parser.parseFromString(data, 'text/html');

                    // Copy stylesheet links from footer.html into document.head (avoid duplicates)
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

                    // Copy inline <style> blocks (avoid exact duplicates)
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

                    // Execute or include any scripts contained in footer.html
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
                            console.warn('Footer script injection skipped:', e);
                        }
                    });

                    const footer = doc.querySelector('footer');
                    if (footer) {
                        // Insert the footer into the placeholder
                        footerPlaceholder.innerHTML = '';
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
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', loadFooter);
} else {
    // DOM is already loaded, call immediately
    loadFooter();
}
