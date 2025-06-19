let currentIndex = 0;
let intervalId;

function showSlide(index) {
    const track = document.getElementById("carouselTrack");
    if (!track) return; // Không tồn tại thì không xử lý

    const items = track.children;
    if (!items.length) return;

    const totalItems = items.length;
    const itemWidth = items[0].offsetWidth;

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
    stopCarousel(); // Dừng trước nếu đã chạy rồi
    intervalId = setInterval(() => {
        showSlide(currentIndex + 1);
    }, 3000);
}

function stopCarousel() {
    if (intervalId) clearInterval(intervalId);
}

window.addEventListener("DOMContentLoaded", () => {
    // Carousel logic
    showSlide(currentIndex);
    startCarousel();

    const carousel = document.querySelector(".featured-product");
    if (carousel) {
        carousel.addEventListener("mouseenter", stopCarousel);
        carousel.addEventListener("mouseleave", startCarousel);
    }

    // === Popup Category Logic ===
    const btn = document.getElementById("categoryBtn");
    const popup = document.getElementById("categoryPopup");
    const categoryItems = document.querySelectorAll(".category-item");
    const subLists = document.querySelectorAll(".sub-list");
    const childItems = document.querySelectorAll(".child-item");
    const childSubLists = document.querySelectorAll(".child-sub-list");

    if (btn && popup) {
        btn.addEventListener("click", () => {
            const isVisible = popup.style.display === "block";
            popup.style.display = isVisible ? "none" : "block";
        });
    }

    // Main category click → activate sub-list
    categoryItems.forEach(item => {
        item.addEventListener("click", () => {
            const index = item.getAttribute("data-index");

            categoryItems.forEach(i => i.classList.remove("active"));
            subLists.forEach(s => s.classList.remove("active"));
            childItems.forEach(c => c.classList.remove("active"));
            childSubLists.forEach(c => c.style.display = "none");

            item.classList.add("active");

            const sub = document.querySelector(`.sub-list[data-index="${index}"]`);
            if (sub) sub.classList.add("active");
        });
    });

    // Sub-category click → activate child-sub-list
    childItems.forEach(item => {
        item.addEventListener("click", () => {
            const subIndex = item.getAttribute("data-sub-index");

            childItems.forEach(c => c.classList.remove("active"));
            childSubLists.forEach(list => list.style.display = "none");

            item.classList.add("active");

            const targetList = document.querySelector(`.child-sub-list[data-sub-index="${subIndex}"]`);
            if (targetList) {
                targetList.style.display = "block";
            }
        });
    });

    // Optional: Auto-select first category
    if (categoryItems.length > 0) {
        categoryItems[0].click();
    }
});
