// carousel.js
let currentIndex = 0;
let intervalId;

function showSlide(index) {
    const track = document.getElementById("carouselTrack");
    const items = track?.querySelectorAll(".carousel-item");
    if (!track || !items.length) return;

    const itemWidth = items[0].offsetWidth + 20;
    const totalItems = items.length;

    currentIndex = index;
    track.style.transition = "transform 0.5s ease-in-out";
    track.style.transform = `translateX(-${currentIndex * itemWidth}px)`;

    if (currentIndex >= totalItems / 2) {
        setTimeout(() => {
            track.style.transition = "none";
            currentIndex = 0;
            track.style.transform = `translateX(0px)`;
        }, 600);
    }
}

function startCarousel() {
    stopCarousel();
    intervalId = setInterval(() => {
        showSlide(currentIndex + 1);
    }, 3000);
}

function stopCarousel() {
    clearInterval(intervalId);
}

window.addEventListener("DOMContentLoaded", () => {
    showSlide(currentIndex);
    startCarousel();

    const carousel = document.querySelector(".featured-product");
    if (carousel) {
        carousel.addEventListener("mouseenter", stopCarousel);
        carousel.addEventListener("mouseleave", startCarousel);
    }

    document.getElementById("prevBtn")?.addEventListener("click", () => {
        stopCarousel();
        showSlide(Math.max(0, currentIndex - 1));
        startCarousel();
    });

    document.getElementById("nextBtn")?.addEventListener("click", () => {
        stopCarousel();
        showSlide(currentIndex + 1);
        startCarousel();
    });
});
