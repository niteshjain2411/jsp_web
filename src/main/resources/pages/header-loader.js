// Header loader script for JSP website
// Include this script in any HTML page to load the shared header

function getHeaderLoaderPath() {
    // Get the script element that loaded this file
    const scriptTags = document.getElementsByTagName('script');
    let loaderPath = null;

    for (let script of scriptTags) {
        if (script.src && script.src.includes('header-loader.js')) {
            loaderPath = script.src;
            break;
        }
    }

    if (!loaderPath && document.currentScript) {
        loaderPath = document.currentScript.src;
    }

    return loaderPath;
}

function resolveHeaderPath() {
    const loaderSrc = getHeaderLoaderPath();

    if (!loaderSrc) {
        console.warn('Could not determine loader script location');
        return null;
    }

    // Extract the directory from the loader script path
    const lastSlashIndex = loaderSrc.lastIndexOf('/');
    const loaderDir = loaderSrc.substring(0, lastSlashIndex + 1);

    // Construct the path to header.html
    const headerPath = loaderDir + 'header.html';

    console.debug('Header loader path resolved to:', headerPath);
    return headerPath;
}

function loadHeader() {
    const headerPlaceholder = document.getElementById('header-placeholder');
    if (!headerPlaceholder) {
        console.error('Header placeholder not found. Make sure to include <div id="header-placeholder"></div> in your HTML.');
        return;
    }

    const headerPath = resolveHeaderPath();
    if (!headerPath) {
        console.error('Failed to resolve header path');
        headerPlaceholder.innerHTML = '<header style="background: #2a7d8c; color: white; padding: 1rem; text-align: center;"><h1>Jain Sangh Pune</h1></header>';
        return;
    }

    fetch(headerPath)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.text();
        })
                .then(data => {
                    // Parse fetched HTML and extract resources + header element
                    const parser = new DOMParser();
                    const doc = parser.parseFromString(data, 'text/html');

                    // Copy stylesheet links from header.html into document.head (avoid duplicates)
                    const links = doc.querySelectorAll('link[rel="stylesheet"]');
                    links.forEach(link => {
                        const href = link.getAttribute('href');
                        if (!href) return;
                        // Skip if identical link already exists
                        if (!document.querySelector(`link[rel="stylesheet"][href="${href}"]`)) {
                            const newLink = document.createElement('link');
                            newLink.rel = 'stylesheet';
                            newLink.href = href;
                            document.head.appendChild(newLink);
                        }
                    });

                    // Copy inline <style> blocks (avoid exact-duplicate content)
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

                    // Execute or include any scripts contained in header.html
                    const scripts = doc.querySelectorAll('script');
                    scripts.forEach(s => {
                        try {
                            if (s.src) {
                                // Avoid duplicate external scripts
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
                            // Ignore script processing errors but log for debugging
                            console.warn('Header script injection skipped:', e);
                        }
                    });

                    const header = doc.querySelector('header');
                    if (header) {
                        // Insert the header into the placeholder
                        headerPlaceholder.innerHTML = '';
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
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', loadHeader);
} else {
    // DOM is already loaded, call immediately
    loadHeader();
}
