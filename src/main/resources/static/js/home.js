document.addEventListener("DOMContentLoaded", function (){
    const btn = document.getElementById("categoryBtn");
    const popup = document.getElementById("categoryPopup");

    btn.addEventListener("click", function (){
        popup.style.display = popup.style.display === "block" ? "none" : "block";
    });

    document.addEventListener("click", function (event){
        if(!popup.contains(event.target) && event.target !== btn){
            popup.style.display = "none";
        }
    });
});