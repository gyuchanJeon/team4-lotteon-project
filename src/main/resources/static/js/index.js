const sectionsData = [
    { id: "Hit", title: "히트상품" },
    { id: "Score", title: "추천상품" },
    { id: "Recent", title: "최신상품" },
    { id: "ScoreMany", title: "인기상품" },
    { id: "Discount", title: "할인상품" }
];

let loading = false;
let currentSectionIndex = 0; // 현재 로드된 섹션의 인덱스
let allProductsLoaded = false; // 전체상품이 로드되었는지 여부
let allProductsPage = 0; // 전체상품을 페이지 단위로 로드
let allProductsLoading = false;
let allLoading = false;

const content = document.getElementById('content');

// 첫 번째 섹션(히트상품) 즉시 로드
function initFirstSection() {
    const firstSectionData = sectionsData[0];
    const firstSection = document.createElement('section');
    firstSection.id = firstSectionData.id;
    firstSection.classList.add('scroll-section');
    firstSection.dataset.loaded = "true"; // 이미 로드된 상태로 설정
    firstSection.innerHTML = `
        <h3><span>${firstSectionData.title}</span></h3>
        <article></article>
    `;
    content.appendChild(firstSection);

    // 첫 번째 섹션 데이터 로드
    loadSectionData(firstSection);
}


// 전체상품 섹션 생성
function createAllProductsSection() {
    const allProductsSection = document.createElement('section');
    allProductsSection.id = "all-products";
    allProductsSection.classList.add('scroll-section');
    allProductsSection.dataset.loaded = "false";
    allProductsSection.innerHTML = `
        <h3><span>전체상품</span></h3>
        <article class="product-list"></article>
    `;

    applySectionAnimation(allProductsSection);
    content.appendChild(allProductsSection);
}


// 한 번에 4개씩 전체상품 데이터 로드
function loadMoreProducts() {
    if (allProductsLoaded || allLoading) return;
    allLoading = true;

    // 전체상품 API 호출, 페이지 번호를 쿼리 파라미터로 전달
    const endpoint = `/lotteon/index/api/all-products?page=${allProductsPage}`;
    console.log(allProductsPage);

    fetch(endpoint)
        .then(response => response.json())
        .then(data => {
            renderAllProducts(data);
            allProductsPage++; // 다음 페이지로 이동

            // 더 이상 로드할 상품이 없으면 전체상품 로드 완료
            if (data.length < 4) {
                allProductsLoaded = true;
            }
            allLoading = false;
        })
        .catch(() => {
            allLoading = false;
        });

}


// 전체상품 데이터 렌더링
function renderAllProducts(products) {
    const productList = document.querySelector('#all-products .product-list');

    // 4개씩 상품을 렌더링
    const productItems = products.map(item => `
        <a href="/lotteon/product/view?productId=${item.productId}">
            <div class="thumb">
                <img src="/lotteon/uploads/product/${item.img1}" alt="상품이미지">
            </div>
            <h2>${item.name}</h2>
            <p>${item.description}</p>
            <div class="org_price">
                <del>${item.price}</del>
                <span>${item.discount}%</span>
            </div>
            <div class="dis_price">
                <ins>${(item.price * (1 - item.discount / 100)).toFixed(0)}</ins>
                ${item.deliveryFee === 0 ? '<span class="free">무료배송</span>' : ''}
            </div>
        </a>
    `).join('');

    productList.innerHTML += productItems;

    // 각 a 태그에 IntersectionObserver를 추가하여 보일 때 애니메이션 적용
    const links = productList.querySelectorAll('a');
    links.forEach(link => {
        const observer = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('visible');
                    observer.unobserve(entry.target); // 애니메이션을 적용한 후 관찰을 중지
                }
            });
        }, { threshold: 0.2 }); // 50% 이상 보일 때 애니메이션 시작

        observer.observe(link);
    });

}

// 스크롤 이벤트에 따라 이후 섹션 동적 생성
window.addEventListener('scroll', () => {
    const scrollPosition = window.scrollY + window.innerHeight;
    const allProductsSection = document.getElementById("all-products");

    // 현재 섹션 인덱스에 맞는 섹션만 생성하도록 처리
    if (!loading && currentSectionIndex < sectionsData.length) {
        const data = sectionsData[currentSectionIndex];
        const section = document.getElementById(data.id);

        // 섹션이 없고, 로드된 섹션의 마지막 위치보다 스크롤 위치가 아래인 경우에만 생성
        if (!section && scrollPosition >= content.offsetTop + content.scrollHeight) {
            const newSection = document.createElement('section');
            newSection.id = data.id;
            newSection.classList.add('scroll-section');
            newSection.dataset.loaded = "false";
            newSection.innerHTML = ` 
                <h3><span>${data.title}</span></h3>
                <article></article>
            `;
            content.appendChild(newSection);

            console.log("Section loaded: " + newSection.id);
            loadSectionData(newSection);
        }
    }

    // 전체상품 섹션 로딩은 모든 섹션이 로드된 후에만
    if (!allProductsLoaded && currentSectionIndex >= sectionsData.length && scrollPosition >= content.offsetTop + content.scrollHeight - 300) {
        if (!allProductsSection || allProductsSection.dataset.loaded === "false" && !allProductsLoading) {
            allProductsLoading = true;
            createAllProductsSection(); // 전체상품 섹션 생성
        }
        else if(allProductsSection.dataset.loaded === "false"){
            loadMoreProducts(); // 처음 4개 상품 로드
        }
    }
});

// 데이터 로드 함수
function loadSectionData(section) {
    if (loading) return;
    loading = true;
    const endpoint = `/lotteon/index/api/${section.id}`; // 섹션별 엔드포인트 설정
    fetch(endpoint)
        .then(response => response.json())
        .then(data => {
            renderProducts(section.querySelector('article'), data);
            section.dataset.loaded = "true"; // 섹션이 로드되었음을 표시
            loading = false;

            // 데이터 로드가 완료된 후, currentSectionIndex 증가시켜 다음 섹션 로드 준비
            currentSectionIndex++;

            // 섹션에 대한 애니메이션 적용
            applySectionAnimation(section);
        })
        .catch(() => {
            loading = false;
        });
}

// article 요소에 데이터 렌더링
function renderProducts(article, data) {
    article.innerHTML = data.map(item => `
        <a href="/lotteon/product/view?productId=${item.productId}">
            <div class="thumb">
                <img src="/lotteon/uploads/product/${item.img1}" alt="상품이미지">
            </div>
            <h2>${item.name}</h2>
            <p>${item.description}</p>
            <div class="org_price">
                <del>${item.price}</del>
                <span>${item.discount}%</span>
            </div>
            <div class="dis_price">
                <ins>${(item.price * (1 - item.discount / 100)).toFixed(0)}</ins>
                ${item.deliveryFee === 0 ? '<span class="free">무료배송</span>' : ''}
            </div>
        </a>
    `).join('');


    // 각 a 태그에 IntersectionObserver를 추가하여 보일 때 애니메이션 적용
    const links = article.querySelectorAll('a');
    links.forEach(link => {
        const observer = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('visible');
                    observer.unobserve(entry.target); // 애니메이션을 적용한 후 관찰을 중지
                }
            });
        }, { threshold: 0.2 }); // 50% 이상 보일 때 애니메이션 시작

        observer.observe(link);
    });
}

// 섹션에 애니메이션을 적용하는 함수
function applySectionAnimation(section) {
    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible'); // 섹션이 화면에 보일 때 애니메이션 적용
                observer.unobserve(entry.target); // 애니메이션을 적용한 후 관찰을 중지
            }
        });
    }, { threshold: 0.2 }); // 섹션이 50% 이상 보일 때 애니메이션 시작

    observer.observe(section);
}

// 첫 번째 섹션 초기화
initFirstSection();