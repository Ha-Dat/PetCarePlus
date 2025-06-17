// document.addEventListener("DOMContentLoaded", function (){
//     const btn = document.getElementById("categoryBtn");
//     const popup = document.getElementById("categoryPopup");
//
//     btn.addEventListener("click", function (){
//         popup.style.display = popup.style.display === "block" ? "none" : "block";
//     });
//
//     document.addEventListener("click", function (event){
//         if(!popup.contains(event.target) && event.target !== btn){
//             popup.style.display = "none";
//         }
//     });
// });

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
});