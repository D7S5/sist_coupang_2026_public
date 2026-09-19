document.addEventListener('DOMContentLoaded', function() {
    // 탭 클릭 이벤트 처리
    const tabButtons = document.querySelectorAll('.tab-btn');
    const authForms = document.querySelectorAll('.auth-form');

    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            const targetTab = this.getAttribute('data-tab');

            // 모든 탭 버튼 비활성화 및 선택된 탭 활성화
            tabButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');

            // 모든 폼 숨기기 및 해당 폼 표시
            authForms.forEach(form => {
                form.classList.remove('active-form');
                if (form.id === `form-${targetTab}`) {
                    form.classList.add('active-form');
                }
            });
        });
    });

    // 입력 필드 클리어 버튼 동작
    const inputs = document.querySelectorAll('.input-group input');
    inputs.forEach(input => {
        input.addEventListener('input', function() {
            const clearBtn = this.nextElementSibling;
            if (clearBtn && clearBtn.classList.contains('btn-clear')) {
                clearBtn.style.display = this.value.length > 0 ? 'block' : 'none';
            }
        });
    });

    const clearButtons = document.querySelectorAll('.btn-clear');
    clearButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            const input = this.previousElementSibling;
            if (input) {
                input.value = '';
                this.style.display = 'none';
                input.focus();
            }
        });
    });
});