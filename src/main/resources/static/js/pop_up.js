// pop_up.js
window.addEventListener("DOMContentLoaded", () => {
    const btn = document.getElementById("categoryBtn");
    const popup = document.getElementById("categoryPopup");
    const categoryItems = document.querySelectorAll(".category-item");
    const subLists = document.querySelectorAll(".sub-list");
    const childItems = document.querySelectorAll(".child-item");
    const childSubLists = document.querySelectorAll(".child-sub-list");
    const grandchildItems = document.querySelectorAll(".grandchild-item");

    if (btn && popup) {
        btn.addEventListener("click", () => {
            popup.style.display = popup.style.display === "block" ? "none" : "block";
        });
    }

    categoryItems.forEach(item => {
        item.addEventListener("click", () => {
            const index = item.getAttribute("data-index");

            categoryItems.forEach(i => i.classList.remove("active"));
            subLists.forEach(s => s.classList.remove("active"));
            childItems.forEach(c => c.classList.remove("active"));
            childSubLists.forEach(c => c.style.display = "none");

            item.classList.add("active");
            document.querySelector(`.sub-list[data-index="${index}"]`)?.classList.add("active");
        });
    });

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

    if (categoryItems.length > 0) {
        categoryItems[0].click();
    }

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

    document.querySelectorAll(".child-item").forEach(item => {
        item.addEventListener("click", () => {
            const subIndex = item.dataset.subIndex;
            const childSubList = document.querySelector(`.child-sub-list[data-sub-index="${subIndex}"]`);
            const hasGrandchild = childSubList && childSubList.querySelectorAll(".grandchild-item").length > 0;

            // Reset trạng thái
            document.querySelectorAll(".child-item").forEach(c => c.classList.remove("active"));
            document.querySelectorAll(".child-sub-list").forEach(list => list.style.display = "none");

            item.classList.add("active");

            if (hasGrandchild) {
                // Có cháu → chỉ hiện danh sách cháu
                childSubList.style.display = "block";
            } else {
                // Không có cháu → chuyển trang
                const parentLi = item.closest(".sub-list");
                const parentIndex = parentLi.getAttribute("data-index");
                const parentName = document.querySelector(`.category-item[data-index="${parentIndex}"] .parent-item`).textContent.trim();
                const keyword = item.textContent.trim();
                window.location.href = `/view-product?&category=${encodeURIComponent(parentName)}`;
            }
        });
    });

    document.querySelectorAll(".grandchild-item").forEach(item => {
        item.addEventListener("click", () => {
            const grandchildName = item.textContent.trim();
            const subList = item.closest(".child-sub-list");
            const subIndex = subList.getAttribute("data-sub-index");
            const [parentIndex] = subIndex.split("-");
            const parentName = document.querySelector(`.category-item[data-index="${parentIndex}"] .parent-item`).textContent.trim();
            const childItem = document.querySelector(`.sub-list[data-index="${parentIndex}"] .child-item[data-sub-index="${subIndex}"]`);
            const keyword = childItem ? childItem.textContent.trim() : "";

            window.location.href = `/view-product?keyword=${encodeURIComponent(keyword)}&category=${encodeURIComponent(parentName)}`;
        });
    });
});
