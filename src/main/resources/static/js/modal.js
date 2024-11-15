window.addEventListener('load', function () {
    const btnclose = document.getElementsByClassName('closeBtn');

    for (let i = 0; i < btnclose.length; i++) {
        btnclose[i].addEventListener('click', function() {
            const modalArticle = this.closest('article.myInfo_modal');
            if (modalArticle) {
                modalArticle.classList.add('Modalhidden');
            }
        });
    }
    const btnCancel = document.getElementsByClassName('cancelBtn');

    for (let i = 0; i < btnCancel.length; i++) {
        btnCancel[i].addEventListener('click', function() {
            const modalArticle = this.closest('article.myInfo_modal');
            if (modalArticle) {
                modalArticle.classList.add('Modalhidden');
            }
        });
    }
});