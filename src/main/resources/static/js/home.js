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

    // Nếu đến hết bản chính (giữa danh sách clone)
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

    const btn = document.getElementById("categoryBtn");
    const popup = document.getElementById("categoryPopup");
    const categoryItems = document.querySelectorAll(".category-item");
    const subLists = document.querySelectorAll(".sub-list");
    const childItems = document.querySelectorAll(".child-item");
    const childSubLists = document.querySelectorAll(".child-sub-list");
    const grandchildItems = document.querySelectorAll(".grandchild-item");

    if (btn && popup) {
        btn.addEventListener("click", () => {
            const isVisible = popup.style.display === "block";
            popup.style.display = isVisible ? "none" : "block";
        });
    }

    // Main category click
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

    // Sub-category click
    childItems.forEach(item => {
        item.addEventListener("click", () => {
            const subIndex = item.getAttribute("data-sub-index");

            childItems.forEach(c => c.classList.remove("active"));
            childSubLists.forEach(list => list.style.display = "none");

            item.classList.add("active");

            const targetList = document.querySelector(`.child-sub-list[data-sub-index="${subIndex}"]`);
            if (targetList) targetList.style.display = "block";
        });
    });

    // Auto-select first category
    if (categoryItems.length > 0) {
        categoryItems[0].click();
    }

    // Click parent category
    document.querySelectorAll(".parent-item").forEach(item => {
        item.addEventListener("click", () => {
            const li = item.closest("li.category-item");
            const index = li.dataset.index;
            const subList = document.querySelector(`.sub-list[data-index="${index}"]`);
            const hasChild = subList && subList.querySelectorAll(".child-item").length > 0;

            if (!hasChild) {
                const categoryName = item.textContent.trim();
                window.location.href = `/view-product?category=${encodeURIComponent(categoryName)}`;
            }
        });
    });

    // Click category con
    document.querySelectorAll(".child-item").forEach(item => {
        item.addEventListener("click", () => {
            const subIndex = item.dataset.subIndex;
            const childSubList = document.querySelector(`.child-sub-list[data-sub-index="${subIndex}"]`);
            const hasGrandchild = childSubList && childSubList.querySelectorAll(".grandchild-item").length > 0;

            if (!hasGrandchild) {
                // Nếu không có cháu → chuyển theo category cha
                const parentLi = item.closest(".sub-list");
                const parentIndex = parentLi.getAttribute("data-index");
                const parentName = document.querySelector(`.category-item[data-index="${parentIndex}"] .parent-item`).textContent.trim();
                window.location.href = `/view-product?category=${encodeURIComponent(parentName)}`;
            }
            // Nếu có cháu → chỉ hiển thị danh sách cháu, không chuyển trang
        });
    });

// Click category cháu
    document.querySelectorAll(".grandchild-item").forEach(item => {
        item.addEventListener("click", () => {
            const grandchildName = item.textContent.trim();

            const subList = item.closest(".child-sub-list");
            const subIndex = subList.getAttribute("data-sub-index");

            // Từ subIndex "0-1", tách thành index cha và con
            const [parentIndex, childIndex] = subIndex.split("-");
            const parentName = document.querySelector(`.category-item[data-index="${parentIndex}"] .parent-item`).textContent.trim();
            const childItem = document.querySelector(`.sub-list[data-index="${parentIndex}"] .child-item[data-sub-index="${subIndex}"]`);
            const keyword = childItem ? childItem.textContent.trim() : "";

            window.location.href = `/view-product?keyword=${encodeURIComponent(keyword)}&category=${encodeURIComponent(parentName)}`;
        });
    });

});
