'use strict';

document.addEventListener("DOMContentLoaded", () => {

    // --- Global Variables ---
    let currentPage = 0;
    const pageSize = 8;
    const prodId = document.getElementById("additional-product-info").dataset.productId;

    function roundToTens(num) {
        return Math.round(num / 10) * 10;
    }

    // --- Discounted Price Calculation ---
    const discountRateNode = document.querySelector('.discount-rate');
    const originalPriceNode = document.querySelector('.original-price');
    const discountedPriceNode = document.querySelector('.discount-price');
    const originalPrice = parseInt(originalPriceNode.getAttribute('data-originalPrice'));
    const discountRate = parseInt(discountRateNode.getAttribute('data-discountRate'));
    const discountedPrice = originalPrice * (1 - discountRate / 100);
    discountedPriceNode.textContent = `${roundToTens(discountedPrice).toLocaleString()}원`;

    function discounted_price(price) {
        return roundToTens(price * (1 - discountRate / 100));
    }

    // --- Product Options Selection ---
    const variantDTO = JSON.parse(document.getElementById('product-dto').textContent);
    const selects = document.querySelectorAll('#variant-options select');

    selects.forEach(select => select.addEventListener('change', findMatchingVariant));

    function findMatchingVariant(e) {
        const selectedOptions = Array.from(selects).map(select => select.value);
        if (selectedOptions.every(value => value)) {
            const selectedOptionsKey = `[${Array.from(selects).map(select => select.previousElementSibling.innerText.replace(':', '').trim()).join(', ')}]`;
            const matchingVariant = variantDTO.productVariants.find(variant =>
                JSON.stringify(variant.options[selectedOptionsKey]) === JSON.stringify(selectedOptions)
            );

            if (matchingVariant) {
                displayVariant(matchingVariant);
            } else {
                document.querySelector('.selected-variant-price').textContent = "제품 가격 : 0원";
            }
        }
    }

    function updateTotalPrice(price, quantity) {
        const totalPrice = document.querySelector('.total-price strong');
        totalPrice.textContent = (parseInt(totalPrice.textContent.replace(",", "").replace("원", "")) + price * quantity).toLocaleString() + "원";
    }

    function displayVariant(matchingVariant) {
        console.log("Matched Variant Price:", matchingVariant.price);
        const total_price = document.querySelector('.total-price');
        const innerHtml = `
            <div id="${matchingVariant.variant_id}" class="selected_variant">
                <input class="variantsIds" type="hidden" value="${matchingVariant.variant_id}">
                <div class="delete_variant_div">
                    <button class="delete_variant" type="button">X</button>                                  
                </div>
                <div class="variant_info_div">
                    <span>${matchingVariant.sku}</span>            
                    <div class="btnPlusMinusDiv">
                        <button class="btn_minus" type="button">-</button>
                        <input class="inputNum" type="number" min="1" max="${matchingVariant.stock}" value="1">
                        <button class="btn_plus" type="button">+</button>
                    </div>
                    <span class="variant_price">${discounted_price(matchingVariant.price).toLocaleString()}원</span>
                </div>
            </div>`;
        if (!document.getElementById(`${matchingVariant.variant_id}`)) {
            total_price.insertAdjacentHTML('beforebegin', innerHtml);
            updateTotalPrice(discounted_price(matchingVariant.price), 1);
            addEventListenersToVariant(matchingVariant);
        }
    }



    function addEventListenersToVariant(matchingVariant) {
        const variantElem = document.getElementById(matchingVariant.variant_id);
        variantElem.querySelector('.delete_variant').addEventListener('click', function () {
            variantElem.remove();
            updateTotalPrice(-discounted_price(matchingVariant.price), parseInt(variantElem.querySelector('.inputNum').value));
        });

        const btn_minus = variantElem.querySelector('.btn_minus');
        const btn_plus = variantElem.querySelector('.btn_plus');
        const inputNum = variantElem.querySelector('.inputNum');
        let previousNum = parseInt(inputNum.value);

        btn_minus.addEventListener('click', () => changeQuantity(inputNum, -1, previousNum));
        btn_plus.addEventListener('click', () => changeQuantity(inputNum, 1, previousNum));
        inputNum.addEventListener('change', () => validateInputNum(inputNum, previousNum));
    }

    function changeQuantity(input, diff, previousNum) {
        let currentNum = parseInt(input.value) + diff;
        if (currentNum > 0 && currentNum <= parseInt(input.max)) {
            input.value = currentNum;
            updateTotalPrice(parseInt(input.parentElement.nextElementSibling.textContent.replace(",", "").replace("원", "")), diff);
            previousNum = currentNum;
        }
    }

    function validateInputNum(input, previousNum) {
        const currentNum = parseInt(input.value);
        if (currentNum > parseInt(input.max)) {
            alert(`해당 상품의 최대 수량은 ${input.max}개 입니다.`);
            input.value = previousNum;
        } else if (currentNum <= 0 || isNaN(currentNum)) {
            alert('유효한 숫자를 입력해주세요');
            input.value = previousNum;
        } else {
            updateTotalPrice(parseInt(input.parentElement.nextElementSibling.textContent.replace(",", "").replace("원", "")), currentNum - previousNum);
            previousNum = currentNum;
        }
    }

    // --- Load Reviews with Pagination ---
    async function loadReviews(page = 0) {
        try {
            const response = await fetch(`/lotteon/review/${prodId}?page=${page}&size=${pageSize}`);
            const data = await response.json();

            if (response.ok) {
                renderReviews(data.content);
                updatePagination(data.totalPages, page);
                currentPage = page;
            } else {
                console.error("리뷰를 불러오는 중 오류 발생:", data);
            }
        } catch (error) {
            console.error("리뷰를 불러오는 중 오류 발생:", error);
        }
    }

    function renderReviews(reviews) {
        const reviewList = document.getElementById("review-list");
        reviewList.innerHTML = reviews.length === 0 ? "<p>리뷰가 없습니다.</p>" : "";

        reviews.forEach(review => {
            const reviewItem = document.createElement("div");
            reviewItem.classList.add("review-item");
            reviewItem.innerHTML = `
                <div class="review-header">
                    <span class="review-author">${review.uid}</span>
                    <span class="review-date">${review.regDate || '등록일 없음'}</span>
                </div>
                <div class="review-rating">${renderStars(review.rating)}</div>
                <div><p class="review_content">${review.content}</p></div>
                <div>
                    ${review.img1 ? `<img class= "review_img" src="/lotteon/uploads/review/${review.img1}" alt="리뷰 이미지1"/>` : ""}
                    ${review.img2 ? `<img class= "review_img" src="/lotteon/uploads/review/${review.img2}" alt="리뷰 이미지2"/>` : ""}
                </div>`;
            reviewList.appendChild(reviewItem);
        });
    }

    function renderStars(rating) {
        let starsHTML = '';
        for (let i = 0; i < Math.floor(rating / 2); i++) starsHTML += '<img src="/lotteon/images/review/star.png" alt="Full Star">';
        if (rating % 2 === 1) starsHTML += '<img src="/lotteon/images/review/star_half.png" alt="Half Star">';
        for (let i = 0; i < 5 - Math.ceil(rating / 2); i++) starsHTML += '<img src="/lotteon/images/review/star_gray.png" alt="Gray Star">';
        return starsHTML;
    }

    function updatePagination(totalPages, currentPage) {
        const paginationDiv = document.querySelector('.pagination');
        paginationDiv.innerHTML = '';

        const prevButton = createPageButton("이전", currentPage === 0, () => loadReviews(currentPage - 1));
        paginationDiv.appendChild(prevButton);

        for (let i = 0; i < totalPages; i++) {
            const pageButton = createPageButton(i + 1, i === currentPage, () => loadReviews(i));
            paginationDiv.appendChild(pageButton);
        }

        const nextButton = createPageButton("다음", currentPage + 1 === totalPages, () => loadReviews(currentPage + 1));
        paginationDiv.appendChild(nextButton);
    }

    function createPageButton(text, disabled, onClick) {
        const button = document.createElement('button');
        button.textContent = text;
        button.disabled = disabled;
        button.onclick = onClick;
        return button;
    }

    loadReviews();

    // --- Modal and Coupon Issue ---
    const couponModal = document.getElementById("couponModal");

    document.getElementById("couponButton")?.addEventListener("click", () => {
        couponModal.style.display = "block";
    });

    document.getElementById("closeCouponModal")?.addEventListener("click", () => {
        couponModal.style.display         = "none";
    });

    window.addEventListener("click", (event) => {
        if (event.target === couponModal) couponModal.style.display = "none";
    });

    // --- 쿠폰 발급 ---
    document.querySelectorAll(".download-btn").forEach(button =>
        button.addEventListener("click", () => issueCoupon(button))
    );

    function issueCoupon(button) {
        const couponId = button.getAttribute("data-coupon-id");
        fetch(`/lotteon/api/coupons/issue`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ couponId })
        })
            .then(response => {
                if (response.ok) {
                    alert("쿠폰이 성공적으로 발급되었습니다.");
                } else if (response.status === 409) {
                    alert("이미 발급된 쿠폰입니다.");
                } else if (response.status === 401) {
                    alert("로그인이 필요합니다.");
                    window.location.href = "/lotteon/member/login"; // 로그인 페이지로 리다이렉트
                } else {
                    alert("쿠폰 발급 중 오류가 발생했습니다.");
                }
            })
            .catch(error => console.error("Error issuing coupon:", error));
    }

    // --- 장바구니 담기 ---
    const btnCart = document.querySelector('.cart-button');
    btnCart?.addEventListener('click', function (event) {
        event.preventDefault(); // 기본 폼 제출 방지

        const variantsIds = Array.from(document.querySelectorAll('.variantsIds')).map(node => parseInt(node.value.trim()));
        const counts = Array.from(document.querySelectorAll('.inputNum')).map(node => parseInt(node.value.trim()));

        const json = {
            productVariants: variantsIds,
            counts: counts
        };

        fetch('/lotteon/product/cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(json)
        })
            .then(response => response.text())
            .then(data => {
                if (data === "success") {
                    if (confirm("장바구니에 상품이 담겼습니다. 장바구니로 이동하시겠습니까?")) {
                        window.location.href = "/lotteon/product/cart";
                    }
                } else if (data === "failed") {
                    alert("장바구니 등록에 실패했습니다.");
                } else if (data === "noUser") {
                    alert("로그인이 필요합니다.");
                    window.location.href = "/lotteon/member/login";
                }
            })
            .catch(error => console.error('Error:', error));
    });

    // --- 바로구매 ---
    const btnBuy = document.querySelector('.buy-button');
    btnBuy?.addEventListener('click', function (event) {
        event.preventDefault();

        const variantsIds = Array.from(document.querySelectorAll('.variantsIds')).map(node => parseInt(node.value.trim()));
        const counts = Array.from(document.querySelectorAll('.inputNum')).map(node => parseInt(node.value.trim()));

        const json = {
            productVariants: variantsIds,
            counts: counts
        };

        fetch('/lotteon/product/order', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(json)
        })
            .then(response => response.text())
            .then(data => {
                if (data === "success") {
                    alert("주문 페이지로 이동합니다.");
                    window.location.href = "/lotteon/product/order";
                } else if (data === "failed") {
                    alert("상품 구매 중 문제가 발생하였습니다.");
                } else if (data === "noUser") {
                    alert("로그인이 필요합니다.");
                    window.location.href = "/lotteon/member/login";
                }
            })
            .catch(error => console.error('Error:', error));
    });

});