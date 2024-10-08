function httpRequest(method, url, body, success, fail) {
    const headers = {
        Authorization: 'Bearer ' + localStorage.getItem('access_token'),
        'Content-Type': 'application/json'  // JSON 형식으로 전송
    };

    fetch(url, {
        method: method,
        headers: headers,
        body: body,
    }).then(response => {
        if (response.ok) {
            return response.json().then(data => success(data, response));  // JSON 응답과 응답 객체 전달
        }
        const refresh_token = getCookie('refresh_token');
        if (response.status === 401 && refresh_token) {
            return fetch('/api/token', {
                method: 'POST',
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    refreshToken: getCookie('refresh_token'),
                }),
            })
                .then(res => res.ok ? res.json() : Promise.reject())
                .then(result => {
                    localStorage.setItem('access_token', result.accessToken);
                    return httpRequest(method, url, body, success, fail); // 토큰 갱신 후 다시 요청
                })
                .catch(() => fail(response));
        } else {
            return fail(response);
        }
    }).catch(error => fail(error));
}


// 좋아요
const likeButton = document.getElementById('like-button');

if (likeButton) {
    likeButton.addEventListener('click', event => {
        const likeIcon = document.getElementById('like-icon');
        const articleId = likeButton.getAttribute('article-id');  // article-id 속성 사용
        const likeCountElement = document.getElementById('like-count');  // 좋아요 수 표시 요소

        const body = JSON.stringify({
            articleId: articleId
        });
        console.log('좋아요 요청 보냄:', body);  // 디버깅용 로그 추가
        function success(response) {
            console.log("Success response:", response);  // 서버 응답 로그 확인
            if (response.likedStatus) {
                likeIcon.classList.add('liked');  // 좋아요 상태일 때 빨간색
                likeIcon.classList.remove('not-liked');
            } else {
                likeIcon.classList.add('not-liked');  // 좋아요 해제 시 원래 색상
                likeIcon.classList.remove('liked');
            }
            console.log("Updated classes:", likeIcon.classList);
            // 좋아요 수 업데이트
            likeCountElement.textContent = response.likeCount;  // 서버에서 반환된 좋아요 수로 업데이트
        }

        function fail(response) {
            alert('좋아요를 실패했습니다. 로그인 후 이용해주세요! ');  // 오류 메시지 출력
        }

        httpRequest('POST', '/api/like', body, success, fail);

    });
}

document.addEventListener('DOMContentLoaded', function () {
    const articleId = document.getElementById('article-id').value;  // articleId 가져오기
    const likeIcon = document.getElementById('like-icon');

    // 서버로 현재 게시글에 대한 좋아요 상태 요청 (userId는 서버에서 처리)
    fetch(`/api/like/status?articleId=${articleId}`, {
        method: 'GET',
        headers: {
            Authorization: 'Bearer ' + localStorage.getItem('access_token'),  // JWT 토큰 전송
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())  // 응답을 JSON으로 파싱
        .then(data => {
            // 좋아요 상태에 따라 하트 색상 변경
            if (data.likedStatus) {
                likeIcon.classList.remove('not-liked');
                likeIcon.classList.add('liked');  // 좋아요 상태이면 빨간색으로 변경
            } else {
                likeIcon.classList.remove('liked');
                likeIcon.classList.add('not-liked');  // 좋아요 상태가 아니면 기본 색으로 설정
            }
        })
        .catch(error => console.error('Error fetching like status:', error));
});
