window.onload = function () {
    const addPoint = document.getElementById("addPoint");
    const allPoint = document.getElementById("allPoint");
    const nowPoint = document.getElementById("nowPoint");
    const all__sold = document.getElementById("all__sold");
    const all__discount = document.getElementById("all__discount");
    const all__deliveryfee = document.getElementById("all__deliveryfee");
    const all__totalPrice = document.getElementById("all__totalPrice");
    const selectElement = document.getElementById('condition');
    const pay__deliveryfee = document.getElementById('pay__deliveryfee');
    const recip__name = document.getElementById('recip__name');
    const phone__number = document.getElementById('phone__number');
    const paymentMethods = document.getElementsByName("paymentMethod");

    //전체 금액 계산용
    const originPrice = document.getElementById("originPrice");
    const deliveryFee = document.getElementById("deliveryFee");


    // 합계를 계산하는 함수 정의
    function totalPrice() {
        const originPrices = document.querySelectorAll("#originPrice");
        let total = Array.from(originPrices).reduce((acc, priceElement) => {
            const price = parseFloat(priceElement.innerText.replace(/,/g, '')) || 0; // 숫자로 변환
            return acc + price;
        }, 0);
        all__sold.innerText = `${total.toLocaleString()}원`;
        console.log('전체 판매금액:', total);
    }
    function deliveryPrice(){
        const deliveryPrice = document.querySelectorAll("#deliveryFee");
        let total = Array.from(deliveryPrice).reduce((acc, priceElement) => {
            const price = parseFloat(priceElement.innerText.replace(/,/g, '')) || 0;
            return acc + price;
        }, 0);
        all__deliveryfee.innerText = `${total.toLocaleString()}원`;
        console.log('전체 배송금액 : ' , total)
    }


    //함수 호출 한번 해줘야함
    totalPrice();
    deliveryPrice()

    // option1 (문자열)
    const option1Element = document.querySelector('.option1');
    const option1Value = () => option1Element.value;
    console.log(option1Value());

    const postcodeInput = document.getElementById("postcode");
    const addressInput = document.getElementById("address");
    const detailAddressInput = document.getElementById("detailAddress");

    // 우편번호 검색 완료 시
    var findPostcodeBtn = document.getElementById("findPostcodeBtn");
    findPostcodeBtn.onclick = function() {
        new daum.Postcode({
            oncomplete: function(data) {
                postcodeInput.value = data.zonecode;
                addressInput.value = data.roadAddress || data.jibunAddress;
                detailAddressInput.focus();
            }
        }).open();
    };

    // 숫자 변환 함수 (콤마와 원 단위 제거)
    function parsePrice(priceText) {
        return parseInt(priceText.replace(/[^0-9]/g, ''), 10) || 0;
    }

    // 최종 계산 수행 함수
    function calculateFinalPrice() {
        let soldPrice = parsePrice(all__sold.innerText);
        let deliveryFee = parsePrice(all__deliveryfee.innerText);

        // 쿠폰 금액 추출
        var selectedOptionText = selectElement.options[selectElement.selectedIndex].text;
        var couponValue = selectedOptionText.match(/\d+/);
        let couponDiscount = couponValue ? parseInt(couponValue[0]) : 0;

        // 사용된 포인트 계산
        let usedPoint = parseInt(addPoint.value) || 0;

        // 할인 계산
        let totalDiscount = couponDiscount + (soldPrice * 0.28) + usedPoint;
        all__discount.innerText = totalDiscount.toLocaleString() + '원';

        // 최종 결제 금액 계산
        let finalPrice = soldPrice - totalDiscount + deliveryFee;
        if (finalPrice < 0) {
            finalPrice = 0;
        }
        all__totalPrice.innerText = finalPrice.toLocaleString() + '원';
    }

    addPoint.addEventListener('input', function () {
        let usedPoint = parseInt(addPoint.value) || 0;

        if (usedPoint < 0) {
            usedPoint = 0;
            addPoint.value = 0;
        }

        if (usedPoint === 5000) {
            addPoint.setAttribute('step', '1000');
        }

        if (usedPoint >= 5000 && usedPoint % 1000 !== 0) {
            alert("5000 이후에는 1000 포인트 단위로 사용 가능합니다.");
            usedPoint = Math.floor(usedPoint / 1000) * 1000;
            addPoint.value = usedPoint;
        }

        let maxPoint = parseInt(nowPoint.innerText) || 0;
        if (usedPoint > maxPoint) {
            usedPoint = maxPoint;
            addPoint.value = maxPoint;
        }

        nowPoint.innerText = (maxPoint - usedPoint) + 'p';
        calculateFinalPrice();
    });

    selectElement.addEventListener('change', function () {
        calculateFinalPrice();
    });

    allPoint.addEventListener('click', function () {
        if (confirm("전체 사용하시겠습니까?")) {
            addPoint.value = parseInt(nowPoint.innerText) || 0;
            nowPoint.innerText = '0p';
            calculateFinalPrice();
        }
    });

    calculateFinalPrice();

    const checkoutBtn = document.querySelector('.checkout-btn');
    checkoutBtn.addEventListener('click', function (event) {
        let usedPoint = parseInt(addPoint.value) || 0;
        if (usedPoint > 0 && usedPoint < 5000) {
            alert('사용 포인트가 5000p 미만입니다. 포인트를 5000p 이상으로 사용하여 주십시오.');
            event.preventDefault();
            return;
        }

        let paymentType = null;
        let status = 0;

        paymentMethods.forEach(method => {
            if (method.checked) {
                paymentType = method.value;
                if (method.value !== '4') {
                    status = 1;
                }
            }
        });

        if (!paymentType) {
            alert("결제 방법을 선택하세요.");
            return;
        }

        if (paymentType === "4") {
            alert("무통장 입금을 선택하셨습니다. 24시간 이내에 입금해주시기 바랍니다.");
        }

        function getProductVariantsId(row) {
            let productVariantsId = row.getAttribute("data-variant-id");
            if (!productVariantsId) {
                // productVariantsId가 없으면 0.5초 후 다시 시도
                setTimeout(() => {
                    productVariantsId = row.getAttribute("data-variant-id");
                    console.log("재확인된 productVariantsId:", productVariantsId);
                }, 500);
            }
            return productVariantsId ? parseInt(productVariantsId) : null;
        }

        const orderItems = Array.from(document.querySelectorAll(".cart-table tbody tr")).map(row => {
            const productVariantsId = getProductVariantsId(row);  // 이 함수를 통해 productVariantsId를 가져옴
            const count = parseInt(row.querySelector(".quantity p").innerText) || 0;
            const originPrice = parseInt(row.querySelector(".price-info .discount-price").innerText.replace(/[^0-9]/g, '')) || 0;
            const originDiscount = parseInt(row.querySelector(".price-info del").innerText.replace(/[^0-9]/g, '')) || 0;
            const originPoint = parseInt(row.querySelector(".shipping-info").innerText.replace(/[^0-9]/g, '')) || 0;

            return {
                productVariants: { variant_id: productVariantsId },
                count,
                originPrice,
                originDiscount,
                originPoint,
                deliveryFee: parseInt(all__deliveryfee.innerText.replace(/[^0-9]/g, '')) || 0,
            };
        });

        const data = {
            Date: YYYYMMDDHHMMSS(new Date()),     // 주문 날짜
            Payment: paymentType,                 // 결제 방법
            Status: status,                       // 결제 상태
            count: orderItems.reduce((acc, item) => acc + item.count, 0),
            couponUse: document.getElementById("condition").value,
            deliveryFee: parseInt(all__deliveryfee.innerText.replace(/[^0-9]/g, '')),
            discount: parseInt(all__sold.innerText.replace(/[^0-9]/g, '')) - parseInt(all__totalPrice.innerText.replace(/[^0-9]/g, '')),
            memberInfo: { memberInfoId: 1 },
            option1: option1Element.innerText,
            price: parseInt(all__sold.innerText.replace(/[^0-9]/g, '')),
            recipZip: postcodeInput.value,
            recipAddr1: addressInput.value,
            recipAddr2: detailAddressInput.value,
            recipHp: phone__number.value,
            recipName: recip__name.value,
            totalPrice: parseInt(all__totalPrice.innerText.replace(/[^0-9]/g, '')),
            usePoint: usedPoint,
            orderItems: orderItems
        };

        console.log(data);

        fetch('/lotteon/product/order/save', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
            .then(response => response.json())
            .then(result => {
                if (result.success) {
                    alert('결제가 완료되었습니다.');
                    window.location.href = '/lotteon/product/complete';
                } else {
                    alert('결제가 실패하였습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('결제 중 오류가 발생하였습니다.');
            });
    });
};

// 현재 날짜 및 시간을 YYYYMMDDHHMMSS 형식으로 변환
function YYYYMMDDHHMMSS(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}
