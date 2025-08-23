/**
 * Rating Stars JavaScript
 * Xử lý hiển thị rating sao cho sản phẩm
 */

// Hàm chính để render rating stars
function renderProductRatingStars(rating, feedbackCount, containerElement, options = {}) {
    const defaultOptions = {
        showRatingValue: true,
        showFeedbackCount: true,
        size: 'medium', // small, medium, large
        maxStars: 5,
        emptyStarSymbol: '★',
        filledStarSymbol: '★'
    };
    
    const config = { ...defaultOptions, ...options };
    
    // Tạo container cho rating
    const ratingContainer = document.createElement('div');
    ratingContainer.className = 'rating-info';
    
    // Tạo stars container
    const starsContainer = document.createElement('div');
    starsContainer.className = `product-rating-stars ${config.size}`;
    
    // Nếu rating = 0, hiển thị 5 sao mờ
    if (rating === 0 || rating === null || rating === undefined) {
        starsContainer.classList.add('no-rating');
        for (let i = 0; i < config.maxStars; i++) {
            const star = document.createElement('span');
            star.className = 'star';
            star.textContent = config.emptyStarSymbol;
            starsContainer.appendChild(star);
        }
    } else {
        // Tính toán số sao filled và half
        const fullStars = Math.floor(rating);
        const hasHalfStar = rating % 1 >= 0.5;
        
        // Render filled stars
        for (let i = 0; i < fullStars; i++) {
            const star = document.createElement('span');
            star.className = 'star filled';
            star.textContent = config.filledStarSymbol;
            starsContainer.appendChild(star);
        }
        
        // Render half star nếu có
        if (hasHalfStar) {
            const star = document.createElement('span');
            star.className = 'star half';
            star.textContent = config.filledStarSymbol;
            starsContainer.appendChild(star);
        }
        
        // Render empty stars còn lại
        const remainingStars = config.maxStars - fullStars - (hasHalfStar ? 1 : 0);
        for (let i = 0; i < remainingStars; i++) {
            const star = document.createElement('span');
            star.className = 'star';
            star.textContent = config.emptyStarSymbol;
            starsContainer.appendChild(star);
        }
    }
    
    // Thêm tooltip
    if (rating > 0) {
        starsContainer.classList.add('rating-tooltip');
        starsContainer.setAttribute('data-tooltip', `${rating.toFixed(1)}/5 sao (${feedbackCount} đánh giá)`);
    } else {
        starsContainer.classList.add('rating-tooltip');
        starsContainer.setAttribute('data-tooltip', 'Chưa có đánh giá');
    }
    
    ratingContainer.appendChild(starsContainer);
    
    // Thêm rating value nếu cần
    if (config.showRatingValue && rating > 0) {
        const ratingValue = document.createElement('span');
        ratingValue.className = 'rating-value';
        ratingValue.textContent = rating.toFixed(1);
        ratingContainer.appendChild(ratingValue);
    }
    
    // Thêm feedback count nếu cần
    if (config.showFeedbackCount && feedbackCount > 0) {
        const feedbackCountElement = document.createElement('span');
        feedbackCountElement.className = 'feedback-count';
        feedbackCountElement.textContent = `(${feedbackCount})`;
        ratingContainer.appendChild(feedbackCountElement);
    }
    
    // Clear container và thêm rating
    containerElement.innerHTML = '';
    containerElement.appendChild(ratingContainer);
}

// Hàm để init rating stars cho tất cả sản phẩm trên trang
function initProductRatingStars() {
    // Tìm tất cả các element có data-product-id
    const productElements = document.querySelectorAll('[data-product-id]');
    
    productElements.forEach(element => {
        const productId = element.getAttribute('data-product-id');
        const rating = parseFloat(element.getAttribute('data-rating') || '0');
        const feedbackCount = parseInt(element.getAttribute('data-feedback-count') || '0');
        
        // Tìm stars container trong product element
        const starsContainer = element.querySelector('.stars, .product-rating-stars-container');
        if (starsContainer) {
            renderProductRatingStars(rating, feedbackCount, starsContainer, {
                showRatingValue: false, // Không hiển thị số rating trên product card
                showFeedbackCount: false, // Không hiển thị số lượng feedback trên product card
                size: 'small'
            });
        }
    });
}

// Hàm helper để update rating stars động (cho AJAX)
function updateProductRatingStars(productId, newRating, newFeedbackCount) {
    const productElements = document.querySelectorAll(`[data-product-id="${productId}"]`);
    
    productElements.forEach(element => {
        // Update attributes
        element.setAttribute('data-rating', newRating);
        element.setAttribute('data-feedback-count', newFeedbackCount);
        
        // Re-render stars
        const starsContainer = element.querySelector('.stars, .product-rating-stars-container');
        if (starsContainer) {
            renderProductRatingStars(newRating, newFeedbackCount, starsContainer, {
                showRatingValue: false,
                showFeedbackCount: false,
                size: 'small'
            });
        }
    });
}

// Auto init khi DOM ready
document.addEventListener('DOMContentLoaded', function() {
    initProductRatingStars();
});

// Export functions cho global use
window.renderProductRatingStars = renderProductRatingStars;
window.initProductRatingStars = initProductRatingStars;
window.updateProductRatingStars = updateProductRatingStars;
