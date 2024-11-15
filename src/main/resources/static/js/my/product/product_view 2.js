'use strict'

window.onload = function () {
    // 할인가격 계산 후 주입
    const originalPriceNode = document.querySelector('.original-price');
    const discountRateNode = document.querySelector('.discount-rate');
    const discountedPriceNode = document.querySelector('.discount-price');

    const originalPrice = parseFloat(originalPriceNode.getAttribute('data-originalPrice'));
    const discountRate = parseFloat(discountRateNode.getAttribute('data-discountRate'));
    const discountedPrice = originalPrice * (1 - discountRate / 100);
    discountedPriceNode.textContent = discountedPrice.toLocaleString() + "원";

    // 재고 0 인 제품 선택 비활성화
    const optionSelectsNodes = document.querySelectorAll('.option-select');





};

document.getElementById('cartForm').onsubmit = function(event) {
    event.preventDefault(); // 기본 폼 제출 방지

    // AJAX 요청
    const formData = new FormData(this);
    console.log('formData : ' + formData);
    fetch(this.action, {
        method: 'POST',
        body: formData,
    })
        .then(response => response.text())
        .then(data => {
            if (data === "success") {
                // 성공 시 알림창 띄우기
                if (confirm("장바구니에 상품이 담겼습니다. 장바구니로 이동하시겠습니까?")) {
                    window.location.href = "/lotteon/product/cart"; // 장바구니 페이지로 리다이렉트
                }
            } else if (data === "failed") {
                alert("장바구니 등록에 실패했습니다.");
            } else if (data === "noUser") {
                alert("로그인이 필요합니다."); // 로그인 필요 알림
                window.location.href = "/lotteon/member/login"; // 로그인 페이지로 리다이렉트
            }
        })
        .catch(error => console.error('Error:', error));
};

document.addEventListener('DOMContentLoaded', function() {

    // 수량이 변경될 때마다 count 필드에 값 업데이트
    document.getElementById('quantity').addEventListener('input', function () {
        document.getElementById('count').value = this.value;
    });

    // 페이지 로드 시 초기 수량 값 설정
    document.getElementById('count').value = document.getElementById('quantity').value;

    const productDataElement = document.getElementById('product-dto');
    const productDTO = JSON.parse(productDataElement.textContent);

    console.log("Loaded productDTO:", productDTO); // 전체 productDTO 객체 확인

    const selects = document.querySelectorAll('#variant-options select');

    function findMatchingVariant() {
        // 선택된 옵션을 배열에 담음
        const selectedOptions = [];
        selects.forEach(select => {
            selectedOptions.push(select.value);
        });
        console.log("Selected Options Array:", selectedOptions);

        // 옵션의 키를 생성
        let selectedOptionsKey = `[${Array.from(selects).map(select => select.previousElementSibling.innerText.replace(':', '').trim()).join(', ')}]`;
        console.log("Generated Key for Selected Options (original):", selectedOptionsKey);

        // 순열 생성 함수
        function generatePermutations(array) {
            if (array.length <= 1) return [array];
            const permutations = [];
            for (let i = 0; i < array.length; i++) {
                const currentElement = array[i];
                const remainingElements = array.slice(0, i).concat(array.slice(i + 1));
                const remainingPermutations = generatePermutations(remainingElements);
                remainingPermutations.forEach(permutation => {
                    permutations.push([currentElement, ...permutation]);
                });
            }
            return permutations;
        }

        // 모든 순열 생성
        const optionsPermutations = generatePermutations(selectedOptions);
        const keysPermutations = generatePermutations(Array.from(selects).map(select => select.previousElementSibling.innerText.replace(':', '').trim()));

        console.log("All Options Permutations:", optionsPermutations);
        console.log("All Keys Permutations:", keysPermutations);

        let matchingVariant = null;

        if (selectedOptions.every(value => value)) {
            // 각 키와 옵션 배열의 모든 순열로 variant 찾기
            for (let keyPerm of keysPermutations) {
                selectedOptionsKey = `[${keyPerm.join(', ')}]`;
                console.log("Trying Key Permutation:", selectedOptionsKey);

                for (let optionsPerm of optionsPermutations) {
                    console.log("Trying Options Permutation:", optionsPerm);

                    matchingVariant = productDTO.productVariants.find(variant => {
                        const variantOptions = variant.options[selectedOptionsKey];
                        console.log("Checking variant:", variant.variant_id, "with options:", variantOptions, "against selected options:", optionsPerm);
                        return JSON.stringify(variantOptions) === JSON.stringify(optionsPerm);
                    });

                    if (matchingVariant) {
                        console.log("Match found with Key:", selectedOptionsKey, "and Options:", optionsPerm);
                        break; // 매칭이 되면 더 이상 반복하지 않음
                    }
                }
                if (matchingVariant) break;
            }
        }

        if (matchingVariant) {
            console.log("Matched Variant ID:", matchingVariant.variant_id);
            document.querySelector('.selected-variant-id').value = matchingVariant.variant_id;
        } else {
            console.log("No matching variant found");
            document.querySelector('.selected-variant-id').value = "선택된 Variant ID: 없음";
        }
    }

    selects.forEach(select => {
        select.addEventListener('change', findMatchingVariant);
    });

});
