
        // Debug logging for Chrome DevTools
        console.log("Community Event Portal initialized");
        console.log("Use Chrome DevTools to inspect elements and debug");

        // Form submission handling
        function submitForm(event) {
            event.preventDefault();
            
            const formData = new FormData(event.target);
            const formOutput = document.getElementById('formOutput');
            
            // Collect form data
            const name = formData.get('fullName');
            const email = formData.get('email');
            const eventType = formData.get('eventType');
            const eventDate = formData.get('eventDate');
            
            // Save user preferences to localStorage
            localStorage.setItem('preferredEventType', eventType);
            localStorage.setItem('userEmail', email);
            
            // Display confirmation
            formOutput.innerHTML = `
                <strong>Registration Successful!</strong><br>
                Name: ${name}<br>
                Email: ${email}<br>
                Event: ${document.getElementById('eventType').selectedOptions[0].text}<br>
                Date: ${eventDate}
            `;
            formOutput.style.display = 'block';
            
            console.log("Form submitted:", {name, email, eventType, eventDate});
        }

        // Phone number validation with onblur event
        function validatePhone(input) {
            const phonePattern = /^\(?([0-9]{3})\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$/;
            const errorDiv = document.getElementById('phoneError');
            
            if (input.value && !phonePattern.test(input.value)) {
                input.classList.add('invalid');
                input.classList.remove('valid');
                errorDiv.textContent = 'Please enter a valid phone number (e.g., (555) 123-4567)';
                errorDiv.style.display = 'block';
            } else if (input.value) {
                input.classList.add('valid');
                input.classList.remove('invalid');
                errorDiv.style.display = 'none';
            } else {
                input.classList.remove('valid', 'invalid');
                errorDiv.style.display = 'none';
            }
        }

        // Event fee display with onchange event
        function showEventFee(select) {
            const feeDiv = document.getElementById('eventFee');
            const selectedOption = select.selectedOptions[0];
            
            if (selectedOption && selectedOption.dataset.fee !== undefined) {
                const fee = selectedOption.dataset.fee;
                feeDiv.innerHTML = fee === '0' 
                    ? '🎉 This event is FREE!' 
                    : `💰 Event Fee: $${fee}`;
                feeDiv.style.display = 'block';
                
                // Save preference
                localStorage.setItem('preferredEventType', select.value);
            } else {
                feeDiv.style.display = 'none';
            }
        }

        // Confirmation with onclick event
        function showConfirmation() {
            setTimeout(() => {
                alert('Thank you for registering! You will receive a confirmation email shortly.');
            }, 100);
        }

        // Image enlargement with ondblclick event
        function enlargeImage(img) {
            const modal = document.getElementById('imageModal');
            const modalImg = document.getElementById('modalImage');
            
            modal.style.display = 'block';
            modalImg.src = img.src;
            modalImg.alt = img.alt;
            
            console.log("Image enlarged:", img.alt);
        }

        function closeModal() {
            document.getElementById('imageModal').style.display = 'none';
        }

        // Character counting with keyboard events
        function countCharacters(textarea) {
            const counter = document.getElementById('charCounter');
            const maxLength = 500;
            const currentLength = textarea.value.length;
            
            counter.textContent = `${currentLength} / ${maxLength} characters`;
            
            if (currentLength > maxLength * 0.8) {
                counter.style.color = '#e74c3c';
            } else {
                counter.style.color = '#666';
            }
            
            console.log("Character count:", currentLength);
        }

        function countFeedbackCharacters(textarea) {
            const counter = document.getElementById('feedbackCharCounter');
            const maxLength = 1000;
            const currentLength = textarea.value.length;
            
            counter.textContent = `${currentLength} / ${maxLength} characters`;
            
            if (currentLength > maxLength * 0.8) {
                counter.style.color = '#e74c3c';
            } else {
                counter.style.color = '#666';
            }
        }

        // Video ready event with oncanplay
        function videoReady() {
            const statusDiv = document.getElementById('videoStatus');
            statusDiv.style.display = 'block';
            setTimeout(() => {
                statusDiv.style.display = 'none';
            }, 3000);
            
            console.log("Video is ready to play");
        }

        // Before unload warning
        function checkFormCompletion() {
            const form = document.querySelector('.registration-form');
            const inputs = form.querySelectorAll('input[required], select[required]');
            let hasContent = false;
            
            inputs.forEach(input => {
                if (input.value.trim() !== '') {
                    hasContent = true;
                }
            });
            
            if (hasContent) {
                return "You have unsaved changes. Are you sure you want to leave?";
            }
        }

        // Local storage management
        function clearPreferences() {
            localStorage.clear();
            sessionStorage.clear();
            alert('All preferences have been cleared!');
            
            // Reset form to defaults
            document.getElementById('eventType').value = '';
            document.getElementById('eventFee').style.display = 'none';
            
            console.log("Preferences cleared");
        }

        // Load saved preferences on page load
        function loadPreferences() {
            const savedEventType = localStorage.getItem('preferredEventType');
            const savedEmail = localStorage.getItem('userEmail');
            
            if (savedEventType) {
                const eventTypeSelect = document.getElementById('eventType');
                eventTypeSelect.value = savedEventType;
                showEventFee(eventTypeSelect);
            }
            
            if (savedEmail) {
                document.getElementById('email').value = savedEmail;
            }
            
            console.log("Preferences loaded:", {savedEventType, savedEmail});
        }

        // Geolocation functionality
        function findNearbyEvents() {
            const locationInfo = document.getElementById('locationInfo');
            const coordinatesP = document.getElementById('coordinates');
            const nearestEventP = document.getElementById('nearestEvent');
            
            if (navigator.geolocation) {
                const options = {
                    enableHighAccuracy: true,
                    timeout: 10000,
                    maximumAge: 300000 // 5 minutes
                };
                
                navigator.geolocation.getCurrentPosition(
                    function(position) {
                        const lat = position.coords.latitude;
                        const lon = position.coords.longitude;
                        const accuracy = position.coords.accuracy;
                        
                        coordinatesP.innerHTML = `
                            <strong>Latitude:</strong> ${lat.toFixed(6)}<br>
                            <strong>Longitude:</strong> ${lon.toFixed(6)}<br>
                            <strong>Accuracy:</strong> ±${Math.round(accuracy)} meters
                        `;
                        
                        // Simulate finding nearest event
                        const events = [
                            {name: "Summer Festival", distance: "0.8"},
                            {name: "Community Cleanup", distance: "1.2"},
                            {name: "Business Fair", distance: "2.1"}
                        ];
                        
                        nearestEventP.innerHTML = `
                            <strong>Nearest Events:</strong><br>
                            🎪 ${events[0].name} - ${events[0].distance} miles away<br>
                            🧹 ${events[1].name} - ${events[1].distance} miles away<br>
                            🏪 ${events[2].name} - ${events[2].distance} miles away
                        `;
                        
                        locationInfo.style.display = 'block';
                        
                        console.log("Location found:", {lat, lon, accuracy});
                    },
                    function(error) {
                        let errorMessage = '';
                        
                        switch(error.code) {
                            case error.PERMISSION_DENIED:
                                errorMessage = "❌ Location access denied. Please enable location services and try again.";
                                break;
                            case error.POSITION_UNAVAILABLE:
                                errorMessage = "❌ Location information unavailable. Please check your device settings.";
                                break;
                            case error.TIMEOUT:
                                errorMessage = "❌ Location request timed out. Please try again.";
                                break;
                            default:
                                errorMessage = "❌ An unknown error occurred while retrieving location.";
                                break;
                        }
                        
                        coordinatesP.innerHTML = errorMessage;
                        nearestEventP.innerHTML = "Unable to determine nearest events without location access.";
                        locationInfo.style.display = 'block';
                        
                        console.error("Geolocation error:", error);
                    },
                    options
                );
            } else {
                coordinatesP.innerHTML = "❌ Geolocation is not supported by this browser.";
                nearestEventP.innerHTML = "Please use a modern browser that supports location services.";
                locationInfo.style.display = 'block';
                
                console.error("Geolocation not supported");
            }
        }

        // Smooth scrolling for navigation links
        document.addEventListener('DOMContentLoaded', function() {
            // Load saved preferences
            loadPreferences();
            
            // Add smooth scrolling to navigation links
            const navLinks = document.querySelectorAll('nav a[href^="#"]');
            navLinks.forEach(link => {
                link.addEventListener('click', function(e) {
                    e.preventDefault();
                    const targetId = this.getAttribute('href');
                    const targetSection = document.querySelector(targetId);
                    
                    if (targetSection) {
                        targetSection.scrollIntoView({
                            behavior: 'smooth',
                            block: 'start'
                        });
                    }
                });
            });
            
            // Add form validation feedback
            const requiredInputs = document.querySelectorAll('input[required], select[required]');
            requiredInputs.forEach(input => {
                input.addEventListener('blur', function() {
                    if (this.value.trim() === '') {
                        this.classList.add('invalid');
                        this.classList.remove('valid');
                    } else {
                        this.classList.add('valid');
                        this.classList.remove('invalid');
                    }
                });
            });
            
            // Add session storage for temporary data
            const form = document.querySelector('.registration-form');
            if (form) {
                form.addEventListener('input', function(e) {
                    if (e.target.type !== 'submit') {
                        sessionStorage.setItem(`temp_${e.target.name}`, e.target.value);
                    }
                });
                
                // Restore temporary data
                const inputs = form.querySelectorAll('input, select, textarea');
                inputs.forEach(input => {
                    const tempValue = sessionStorage.getItem(`temp_${input.name}`);
                    if (tempValue && !input.value) {
                        input.value = tempValue;
                    }
                });
            }
            
            // Close modal when clicking outside of it
            const modal = document.getElementById('imageModal');
            modal.addEventListener('click', function(e) {
                if (e.target === modal) {
                    closeModal();
                }
            });
            
            // Keyboard navigation for modal
            document.addEventListener('keydown', function(e) {
                if (e.key === 'Escape') {
                    closeModal();
                }
            });
            
            // Add debugging breakpoints (can be activated in Chrome DevTools)
            console.log("DOM loaded - set breakpoints here for debugging");
            debugger; // This will pause execution when DevTools is open
            
            // Log all form interactions for debugging
            document.addEventListener('change', function(e) {
                if (e.target.form) {
                    console.log('Form input changed:', {
                        field: e.target.name,
                        value: e.target.value,
                        type: e.target.type
                    });
                }
            });
            
            console.log("Community Event Portal fully loaded and initialized");
        });

        // Before unload event listener
        window.addEventListener('beforeunload', function(e) {
            const warning = checkFormCompletion();
            if (warning) {
                e.preventDefault();
                e.returnValue = warning;
                return warning;
            }
        });

        // Additional debugging functions for Chrome DevTools
        window.debugPortal = {
            showLocalStorage: function() {
                console.table(localStorage);
            },
            showSessionStorage: function() {
                console.table(sessionStorage);
            },
            clearAllData: function() {
                localStorage.clear();
                sessionStorage.clear();
                console.log("All storage cleared");
            },
            getFormData: function() {
                const form = document.querySelector('.registration-form');
                const formData = new FormData(form);
                const data = {};
                for (let [key, value] of formData.entries()) {
                    data[key] = value;
                }
                console.table(data);
                return data;
            },
            simulateError: function() {
                throw new Error("Simulated error for debugging purposes");
            }
        };

        // Performance monitoring
        window.addEventListener('load', function() {
            const loadTime = performance.now();
            console.log(`Page fully loaded in ${loadTime.toFixed(2)}ms`);
            
            // Log performance metrics
            if ('performance' in window) {
                const perfData = performance.getEntriesByType('navigation')[0];
                console.table({
                    'DNS Lookup': perfData.domainLookupEnd - perfData.domainLookupStart,
                    'TCP Connection': perfData.connectEnd - perfData.connectStart,
                    'Request': perfData.responseStart - perfData.requestStart,
                    'Response': perfData.responseEnd - perfData.responseStart,
                    'DOM Processing': perfData.domComplete - perfData.domLoading,
                    'Total Load Time': perfData.loadEventEnd - perfData.navigationStart
                });
            }
        });

        // Error handling and logging
        window.addEventListener('error', function(e) {
            console.error('JavaScript Error:', {
                message: e.message,
                filename: e.filename,
                line: e.lineno,
                column: e.colno,
                error: e.error
            });
        });

        // Unhandled promise rejection logging
        window.addEventListener('unhandledrejection', function(e) {
            console.error('Unhandled Promise Rejection:', e.reason);
        });

        // Additional utility functions for debugging
        function logElementInfo(selector) {
            const element = document.querySelector(selector);
            if (element) {
                console.log('Element Info:', {
                    tagName: element.tagName,
                    id: element.id,
                    className: element.className,
                    innerHTML: element.innerHTML.substring(0, 100) + '...',
                    styles: window.getComputedStyle(element)
                });
            } else {
                console.warn('Element not found:', selector);
            }
        }

        function highlightElement(selector) {
            const element = document.querySelector(selector);
            if (element) {
                element.style.outline = '3px solid red';
                element.style.backgroundColor = 'yellow';
                setTimeout(() => {
                    element.style.outline = '';
                    element.style.backgroundColor = '';
                }, 3000);
            }
        }

        // Make debugging functions globally available
        window.logElementInfo = logElementInfo;
        window.highlightElement = highlightElement;

        console.log("Debugging tools loaded. Use debugPortal object, logElementInfo(), and highlightElement() in console.");
    
