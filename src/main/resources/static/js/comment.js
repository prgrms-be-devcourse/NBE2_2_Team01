// // 댓글 작성 요청 함수
// function submitComment(articleId, commentContent, parentCommentId) {
//     const requestBody = {
//         commentContent: commentContent,
//         parentCommentId: parentCommentId
//     };
//
//     // AJAX 요청으로 댓글 작성
//     fetch(`/api/comment/${articleId}`, {
//         method: 'POST',
//         headers: {
//             'Content-Type': 'application/json',
//             'Authorization': 'Bearer ' + localStorage.getItem('access_token')
//         },
//         body: JSON.stringify(requestBody)
//     }).then(response => response.json())
//         .then(comment => {
//             addCommentToDOM(comment);
//             document.querySelector('#comment-form textarea').value = ''; // 입력 필드 초기화
//         })
//         .catch(error => console.error('Error:', error));
// }
//
// // DOM에 새 댓글을 추가하는 함수
// function addCommentToDOM(comment) {
//     const commentSection = document.getElementById('comments-section');
//
//     // 새 댓글 요소 생성
//     const commentCard = document.createElement('div');
//     commentCard.classList.add('card', 'mb-3');
//
//     // 부모 댓글이면 일반 댓글, 대댓글이면 들여쓰기
//     let indentStyle = '';
//     if (comment.parentCommentId) {
//         indentStyle = 'margin-left: 20px;';
//     }
//
//     commentCard.innerHTML = `
//         <div class="card-body" style="${indentStyle}">
//             <h6 class="card-subtitle mb-2 text-muted">${comment.commentAuthor}</h6>
//             <p class="card-text">${comment.commentContent}</p>
//             <p class="text-muted">${new Date(comment.commentCreatedAt).toLocaleString()}</p>
//             <button class="btn btn-link reply-btn" data-comment-id="${comment.commentId}">Reply</button>
//         </div>
//     `;
//
//     // 댓글 섹션에 새 댓글 추가
//     commentSection.appendChild(commentCard);
//
//     // 대댓글 작성 폼을 보여줄 수 있는 이벤트 추가
//     addReplyButtonEvent();
// }
//
// // 댓글 작성 버튼 이벤트
// document.getElementById('comment-form').addEventListener('submit', function (e) {
//     e.preventDefault(); // 폼 기본 동작 막기
//     const commentContent = this.querySelector('textarea').value;
//     const articleId = this.querySelector('input[name="articleId"]').value;
//     submitComment(articleId, commentContent, null); // 부모 댓글 없이 새로운 댓글 추가
// });
//
// // 대댓글 작성 폼 추가
// function addReplyButtonEvent() {
//     document.querySelectorAll('.reply-btn').forEach(button => {
//         button.addEventListener('click', function () {
//             const commentId = this.dataset.commentId;
//             if (document.querySelector(`#reply-form-${commentId}`)) return; // 이미 대댓글 폼이 있을 경우 무시
//
//             const replyForm = document.createElement('form');
//             replyForm.id = `reply-form-${commentId}`;
//             replyForm.innerHTML = `
//                 <div class="form-group mt-2">
//                     <textarea class="form-control" rows="2" placeholder="대댓글을 입력하세요"></textarea>
//                     <button type="button"  id="recomment-btn" class="btn btn-primary btn-sm mt-2 submit-reply-btn" data-comment-id="${commentId}">대댓글 작성</button>
//                 </div>
//             `;
//
//             // 대댓글 작성 폼을 댓글 아래에 추가
//             this.parentElement.appendChild(replyForm);
//
//             // 대댓글 작성 이벤트 연결
//             document.querySelector(`#reply-form-${commentId} .submit-reply-btn`).addEventListener('click', function () {
//                 const replyContent = replyForm.querySelector('textarea').value;
//                 const articleId = document.querySelector('input[name="articleId"]').value;
//                 submitComment(articleId, replyContent, commentId); // 부모 댓글 ID와 함께 전송
//                 replyForm.remove(); // 대댓글 작성 후 폼 제거
//             });
//         });
//     });
// }
//
// // 초기 댓글 목록 로드 시 대댓글 버튼에 이벤트 연결
// addReplyButtonEvent();


// // HTTP 요청을 보내는 함수
// function httpRequest(method, url, body, success, fail) {
//     const headers = {
//         Authorization: 'Bearer ' + localStorage.getItem('access_token'),
//         'Content-Type': 'application/json'  // JSON 형식으로 전송
//     };
//
//     fetch(url, {
//         method: method,
//         headers: headers,
//         body: body,
//     }).then(response => {
//         if (response.ok) {
//             return success(response);  // 응답을 success 콜백으로 전달
//         }
//         const refresh_token = getCookie('refresh_token');
//         if (response.status === 401 && refresh_token) {
//             fetch('/api/token', {
//                 method: 'POST',
//                 headers: {
//                     Authorization: 'Bearer ' + localStorage.getItem('access_token'),
//                     'Content-Type': 'application/json'
//                 },
//                 body: JSON.stringify({
//                     refreshToken: getCookie('refresh_token'),
//                 }),
//             })
//                 .then(res => res.ok ? res.json() : Promise.reject())
//                 .then(result => {
//                     localStorage.setItem('access_token', result.accessToken);
//                     httpRequest(method, url, body, success, fail); // 토큰 갱신 후 다시 요청
//                 })
//                 .catch(() => fail(response));
//         } else {
//             return fail(response);
//         }
//     }).catch(error => fail(error));
// }
//
// // 댓글 작성 요청
// function submitComment(articleId, content, parentCommentId = null) {
//     const body = {
//         commentContent: content,
//         parentCommentId: parentCommentId
//     };
//
//     const url = `/api/comment/${articleId}`;
//     httpRequest('POST', url, JSON.stringify(body),  // JSON으로 변환해서 전송
//         () => {
//             alert('댓글이 추가되었습니다.');
//             loadComments(articleId); // 댓글 목록 다시 로드
//             // 댓글 작성 폼의 textarea 비우기 (새 댓글 작성 시)
//             if (!parentCommentId) {
//                 document.querySelector('textarea[name="content"]').value = ''; // 메인 댓글 textarea 초기화
//             } else {
//                 document.querySelector(`#reply-form-${parentCommentId} textarea`).value = ''; // 대댓글 textarea 초기화
//             }
//         },
//         (error) => {
//             console.error('댓글 추가 실패:', error);
//             alert('댓글 추가에 실패했습니다.');
//         });
// }
//
// // 댓글 목록 로드
// function loadComments(articleId) {
//     const url = `/api/comment/${articleId}`;
//     httpRequest('GET', url, null,
//         (response) => {
//             if (!response.ok) {
//                 console.error('응답에 문제가 있습니다. 상태 코드:', response.status);
//                 return;
//             }
//             response.json().then(comments => {
//                 renderComments(comments); // 댓글 목록을 화면에 렌더링
//             }).catch(error => {
//                 console.error('JSON 파싱 오류:', error);
//             });
//         },
//         (error) => {
//             console.error('댓글 불러오기 실패:', error);
//         });
// }
//
// // 댓글 목록 렌더링
// function renderComments(comments) {
//     const commentSection = document.getElementById('comments-section');
//     commentSection.innerHTML = ''; // 기존 댓글 목록 초기화
//
//     // 부모 댓글과 대댓글을 트리 구조로 렌더링
//     const topLevelComments = comments.filter(comment => !comment.parentCommentId);  // 최상위 부모 댓글들만 필터링
//
//     topLevelComments.forEach(parentComment => {
//         renderCommentWithReplies(parentComment, comments, 0);  // 부모 댓글과 대댓글 렌더링
//     });
// }
//
// // 특정 댓글과 그 대댓글을 렌더링하는 함수
// function renderCommentWithReplies(comment, allComments, depth) {
//     const commentSection = document.getElementById('comments-section');
//
//     // 대댓글의 최대 깊이 설정 (2단계까지 들여쓰기 허용)
//     const maxDepth = 1;
//     const actualDepth = Math.min(depth, maxDepth);
//
//     // 댓글 카드 생성
//     const commentCard = document.createElement('div');
//     // commentCard.classList.add('card', 'mb-3');
//     commentCard.style.marginLeft = `${actualDepth * 20}px`;  // 들여쓰기 (depth에 따라)
//
//     // 대댓글일 경우 색상 지정
//     const isReply = comment.parentCommentId !== null;
//     if (isReply) {
//         commentCard.classList.add('comment-reply'); // 대댓글 클래스 추가
//     } else {
//         commentCard.classList.add('comment-main'); // 새 댓글 클래스 추가
//     }
//
//
//     commentCard.innerHTML = `
//         <div class="card-body">
//             <div class="d-flex justify-content-between align-items-center">
//                 <h6 class="card-subtitle mb-2 text-muted" id="comment-Author">${comment.commentAuthor}</h6>
//                 <div class="comment-button">
//                     <button type="button" id="comment-modify-btn" class="btn btn-primary btn-sm">수정</button>
//                     <button type="button" id="comment-delete-btn" class="btn btn-secondary btn-sm">삭제</button>
//                  </div>
//             </div>
//
//             <p class="card-text" id="comment-Content" >${comment.commentContent}</p>
//             <p class="commentCreatedAt">${comment.commentCreatedAt}</p>
//             <button class="btn btn-link reply-btn" id="reply-button" data-comment-id="${comment.commentId}">댓글 쓰기</button>
//             <div id="reply-form-${comment.commentId}" class="reply-form mt-2" style="display: none;">
//                 <textarea class="form-control" rows="3" placeholder="대댓글을 입력하세요"></textarea>
//                 <div class="text-right">
//                 <button type="button" class="btn btn-primary btn-sm mt-2 submit-reply-btn" data-comment-id="${comment.commentId}">댓글 등록</button>
//                 </div>
//             </div>
//         </div>
//     `;
//
//     // 댓글 섹션에 추가
//     commentSection.appendChild(commentCard);
//
//     // 대댓글 작성 버튼에 이벤트 리스너 추가
//     const replyButton = commentCard.querySelector('.reply-btn');
//     replyButton.addEventListener('click', (event) => {
//         const commentId = event.target.getAttribute('data-comment-id');
//         const replyForm = document.getElementById(`reply-form-${commentId}`);
//         replyForm.style.display = replyForm.style.display === 'none' ? 'block' : 'none';
//     });
//
//     // 대댓글 작성 버튼에 이벤트 리스너 추가
//     const submitReplyButton = commentCard.querySelector('.submit-reply-btn');
//     submitReplyButton.addEventListener('click', (event) => {
//         const parentCommentId = event.target.getAttribute('data-comment-id');
//         const content = document.querySelector(`#reply-form-${parentCommentId} textarea`).value;
//         if (content) {
//             const articleId = document.getElementById('article-id').value;
//             submitComment(articleId, content, parentCommentId);
//         } else {
//             alert('대댓글 내용을 입력하세요.');
//         }
//     });
//
//     // 해당 댓글의 대댓글들을 찾고 렌더링
//     const childComments = allComments.filter(c => c.parentCommentId === comment.commentId);
//     childComments.forEach(childComment => {
//         renderCommentWithReplies(childComment, allComments, depth + 1);  // 대댓글 렌더링 (들여쓰기 증가)
//     });
// }
//
// // 댓글 작성 버튼 클릭 시
// const commentButton = document.getElementById('comment-btn');
// if(commentButton) {
//     commentButton.addEventListener('click', (event) => {
//         event.preventDefault();  // 기본 동작 막기
//         const articleId = document.getElementById('article-id').value;
//         const content = document.querySelector('textarea[name="content"]').value;
//
//         if (content) {
//             submitComment(articleId, content);
//         } else {
//             alert('댓글 내용을 입력해주세요.');
//         }
//     });
// }
//
// //댓글 수정
// const comment_modifyButton = document.getElementById('comment-modify-btn');
// if(comment_modifyButton){
//     comment_modifyButton.addEventListener('click', (event) => {
//
//     });
// }
//
// //댓글 삭제
// const comment_deleteButton = document.getElementById('comment-delete-btn');
// if(comment_deleteButton){
//     comment_deleteButton.addEventListener('click', (event) => {
//
//     });
// }
//
// // 페이지 로드 시 댓글 목록 로드
// window.onload = () => {
//     const articleId = document.getElementById('article-id').value;
//     loadComments(articleId);
// };


// HTTP 요청을 보내는 함수
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
            return success(response);  // 응답을 success 콜백으로 전달
        }
        const refresh_token = getCookie('refresh_token');
        if (response.status === 401 && refresh_token) {
            fetch('/api/token', {
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
                    httpRequest(method, url, body, success, fail); // 토큰 갱신 후 다시 요청
                })
                .catch(() => fail(response));
        } else {
            return fail(response);
        }
    }).catch(error => fail(error));
}

// 댓글 작성 요청
function submitComment(articleId, content, parentCommentId = null) {
    const body = {
        commentContent: content,
        parentCommentId: parentCommentId  // 부모 댓글 ID가 있을 경우 대댓글로 처리
    };

    const url = `/api/comment/${articleId}`;
    httpRequest('POST', url, JSON.stringify(body),  // JSON으로 변환해서 전송
        () => {
            loadComments(articleId); // 댓글 목록 다시 로드
            if (!parentCommentId) {
                document.querySelector('textarea[name="content"]').value = ''; // 메인 댓글 textarea 초기화
            } else {
                document.querySelector(`#reply-form-${parentCommentId} textarea`).value = ''; // 대댓글 textarea 초기화
            }
        },
        (error) => {
            console.error('댓글 추가 실패:', error);
            alert('댓글 추가에 실패했습니다.');
        });
}

// 댓글 수정 요청 함수
function enableEditComment(commentId, currentContent, commentAuthor) {
    const commentCard = document.getElementById(`comment-card-${commentId}`);

    // 기존 댓글 내용을 textarea로 변경
    commentCard.innerHTML = `
        <h6 class="mb-2 text-muted">${commentAuthor}</h6>
        <form id="comment-form">
            <input type="hidden" name="commentId" value="${commentId}"/>
            <div class="form-group">
                <textarea class="form-control" name="content" rows="3">${currentContent}</textarea>  <!-- 기존 내용 유지 -->
            </div>
            <div class="text-right">
                <button type="button" class="btn btn-primary btn-sm" id="save-comment-btn-${commentId}">댓글 등록</button>
            </div>
        </form>
    `;

    const saveCommentButton = document.getElementById(`save-comment-btn-${commentId}`);
    saveCommentButton.addEventListener('click', () => {
        const updatedContent = document.querySelector(`#comment-card-${commentId} textarea[name="content"]`).value;
        submitEdit(commentId, updatedContent);  // 수정된 내용 저장
    });
}

// 댓글 수정 저장 요청
function submitEdit(commentId, updatedContent) {
    const body = JSON.stringify({
        commentContent: updatedContent,
    });

    function success() {
        alert('수정 완료되었습니다.');
        loadComments(document.getElementById('article-id').value);  // 수정 후 댓글 목록 새로고침
    }

    function fail(response) {
        response.text().then(text => {
            alert('수정 실패했습니다.');
        });
    }

    httpRequest('PUT', `/api/comment/${commentId}`, body, success, fail);
}

// 댓글 삭제 요청 함수
function deleteComment(commentId) {
    function success() {
        // 부모 댓글 삭제 시 해당 댓글을 '삭제된 댓글입니다'로 변경
        const commentCard = document.getElementById(`comment-card-${commentId}`);
        if (commentCard) {
            commentCard.innerHTML = `<p>삭제된 댓글입니다</p>`;
        }
        alert('댓글이 삭제되었습니다.');
    }

    function fail(response) {
        response.text().then(text => {
            alert('댓글 삭제에 실패했습니다.');
        });
    }

}

// 댓글 목록 로드
function loadComments(articleId) {
    const url = `/api/comment/${articleId}`;
    httpRequest('GET', url, null,
        (response) => {
            if (!response.ok) {
                console.error('응답에 문제가 있습니다. 상태 코드:', response.status);
                return;
            }
            response.json().then(comments => {
                renderComments(comments); // 댓글 목록을 화면에 렌더링
            }).catch(error => {
                console.error('JSON 파싱 오류:', error);
            });
        },
        (error) => {
            console.error('댓글 불러오기 실패:', error);
        });
}

// 댓글 목록 렌더링
function renderComments(comments) {
    const commentSection = document.getElementById('comments-section');
    commentSection.innerHTML = ''; // 기존 댓글 목록 초기화

    // 부모 댓글과 대댓글을 트리 구조로 렌더링
    const topLevelComments = comments.filter(comment => !comment.parentCommentId);  // 최상위 부모 댓글들만 필터링

    topLevelComments.forEach(parentComment => {
        renderCommentWithReplies(parentComment, comments, 0);  // 부모 댓글과 대댓글 렌더링
    });
}

// 특정 댓글과 그 대댓글을 렌더링하는 함수
// 특정 댓글과 그 대댓글을 렌더링하는 함수
function renderCommentWithReplies(comment, allComments, depth) {
    const commentSection = document.getElementById('comments-section');
    const commentCard = document.createElement('div');
    commentCard.style.marginLeft = `${depth * 20}px`;  // 대댓글 들여쓰기

    // commentId로 고유한 div 설정
    commentCard.id = `comment-card-${comment.commentId}`;

    const isReply = comment.parentCommentId !== null;
    if (isReply) {
        commentCard.classList.add('comment-reply');
    } else {
        commentCard.classList.add('comment-main');
    }

    // 부모 댓글이 삭제되었을 경우
    if (!comment.commentContent) {
        commentCard.innerHTML = `
            <div class="card-body">
                <p>삭제된 댓글입니다</p>
            </div>
        `;
    } else {
        commentCard.innerHTML = `
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center">
                    <h6 class="card-subtitle mb-2 text-muted" id="comment-Author">${comment.commentAuthor}</h6>
                    <div class="comment-button">
                        <button type="button" id="comment-modify-btn-${comment.commentId}" class="btn btn-primary btn-sm">수정</button>
                        <button type="button" id="comment-delete-btn-${comment.commentId}" class="btn btn-secondary btn-sm">삭제</button>
                    </div>
                </div>
                <p class="card-text" id="comment-${comment.commentId}-content">${comment.commentContent}</p>
                <p class="commentCreatedAt">${comment.commentCreatedAt}</p>
                <button class="btn btn-link reply-btn" id="reply-button-${comment.commentId}" data-comment-id="${comment.commentId}">댓글 쓰기</button>
                <div id="reply-form-${comment.commentId}" class="reply-form mt-2" style="display: none;">
                    <textarea class="form-control" rows="3" placeholder="대댓글을 입력하세요"></textarea>
                    <div class="text-right">
                        <button type="button" class="btn btn-primary btn-sm mt-2 submit-reply-btn" data-comment-id="${comment.commentId}">댓글 등록</button>
                    </div>
                </div>
            </div>
        `;
    }

    commentSection.appendChild(commentCard);

    // 수정 버튼에 이벤트 리스너 추가
    const modifyButton = document.getElementById(`comment-modify-btn-${comment.commentId}`);
    modifyButton.addEventListener('click', () => enableEditComment(comment.commentId, comment.commentContent, comment.commentAuthor));

    // 삭제 버튼에 이벤트 리스너 추가
    const deleteButton = document.getElementById(`comment-delete-btn-${comment.commentId}`);
    deleteButton.addEventListener('click', () => deleteComment(comment.commentId));

    // 대댓글 작성 버튼에 이벤트 리스너 추가
    const replyButton = document.getElementById(`reply-button-${comment.commentId}`);
    replyButton.addEventListener('click', (event) => {
        const commentId = event.target.getAttribute('data-comment-id');
        const replyForm = document.getElementById(`reply-form-${commentId}`);
        replyForm.style.display = replyForm.style.display === 'none' ? 'block' : 'none';
    });

    // 대댓글 등록 버튼에 이벤트 리스너 추가
    const submitReplyButton = document.querySelector(`.submit-reply-btn[data-comment-id="${comment.commentId}"]`);
    submitReplyButton.addEventListener('click', (event) => {
        const parentCommentId = event.target.getAttribute('data-comment-id');
        const content = document.querySelector(`#reply-form-${parentCommentId} textarea`).value;
        if (content) {
            const articleId = document.getElementById('article-id').value;
            submitComment(articleId, content, parentCommentId);  // 부모 댓글 ID와 함께 대댓글 등록
        } else {
            alert('대댓글 내용을 입력하세요.');
        }
    });

    // 해당 댓글의 대댓글들을 찾고 렌더링
    const childComments = allComments.filter(c => c.parentCommentId === comment.commentId);
    childComments.forEach(childComment => {
        renderCommentWithReplies(childComment, allComments, depth + 1);  // 대댓글 렌더링 (들여쓰기 증가)
    });
}

// 댓글 작성 버튼 클릭 시
const commentButton = document.getElementById('comment-btn');
if(commentButton) {
    commentButton.addEventListener('click', (event) => {
        event.preventDefault();  // 기본 동작 막기
        const articleId = document.getElementById('article-id').value;
        const content = document.querySelector('textarea[name="content"]').value;

        if (content) {
            submitComment(articleId, content);  // 메인 댓글 등록
        } else {
            alert('댓글 내용을 입력해주세요.');
        }
    });
}

// 페이지 로드 시 댓글 목록 로드
window.onload = () => {
    const articleId = document.getElementById('article-id').value;
    loadComments(articleId);  // 댓글 목록 로드
};
