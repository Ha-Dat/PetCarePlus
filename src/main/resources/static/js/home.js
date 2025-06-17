// Caroucel Show

let currentIndex = 0;
let intervalId;

function showSlide(index) {
    const track = document.getElementById("carouselTrack");
    const items = track.children;
    const totalItems = items.length;
    const itemWidth = items[0].offsetWidth;

    currentIndex = index;
    track.style.transition = "transform 0.5s ease-in-out";
    track.style.transform = `translateX(-${currentIndex * itemWidth}px)`;

    // Reset về slide đầu nếu đến cuối (vì đã nhân đôi list)
    if (currentIndex === totalItems / 2) {
        setTimeout(() => {
            track.style.transition = "none";
            currentIndex = 0;
            track.style.transform = `translateX(0px)`;
        }, 600); // đợi cho animation hoàn tất
    }
}

function startCarousel() {
    intervalId = setInterval(() => {
        showSlide(currentIndex + 1);
    }, 3000);
}

function stopCarousel() {
    clearInterval(intervalId);
}

window.addEventListener("load", () => {
    showSlide(currentIndex);
    startCarousel();

    const carousel = document.querySelector(".featured-product");
    carousel.addEventListener("mouseenter", stopCarousel);
    carousel.addEventListener("mouseleave", startCarousel);

    // Popup logic
    const btn = document.getElementById("categoryBtn");
    const popup = document.getElementById("categoryPopup");
    const categoryItems = document.querySelectorAll(".category-item");
    const subLists = document.querySelectorAll(".sub-list");

    btn.addEventListener("click", () => {
        popup.style.display = popup.style.display === "none" ? "block" : "none";
    });

    categoryItems.forEach(item => {
        item.addEventListener("click", () => {
            const index = item.getAttribute("data-index");

            categoryItems.forEach(i => i.classList.remove("active"));
            subLists.forEach(s => s.classList.remove("active"));

            item.classList.add("active");
            const sub = document.querySelector(`.sub-list[data-index="${index}"]`);
            if (sub) sub.classList.add("active");
        });
    });

    if (categoryItems.length > 0) {
        categoryItems[0].click();
    }
});