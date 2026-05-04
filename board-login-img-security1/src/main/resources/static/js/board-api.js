/**
 * JWT 저장 및 /api 호출 헬퍼 (순수 fetch, 의존성 없음)
 */
const BOARD1_JWT_KEY = 'board1_jwt';
const BOARD1_USER_KEY = 'board1_username';

function board1GetToken() {
    return localStorage.getItem(BOARD1_JWT_KEY);
}

function board1GetUsername() {
    return localStorage.getItem(BOARD1_USER_KEY);
}

function board1SetToken(token) {
    if (token) {
        localStorage.setItem(BOARD1_JWT_KEY, token);
    }
    else {
        localStorage.removeItem(BOARD1_JWT_KEY);
    }
}

/** 로그인 직후: JWT + 표시용 로그인 아이디 */
function board1SetAuth(accessToken, username) {
    board1SetToken(accessToken);
    if (username) {
        localStorage.setItem(BOARD1_USER_KEY, username);
    }
}

function board1ClearToken() {
    localStorage.removeItem(BOARD1_JWT_KEY);
    localStorage.removeItem(BOARD1_USER_KEY);
}

function board1AuthHeaders() {
    const t = board1GetToken();
    const h = { 'Accept': 'application/json' };
    if (t) {
        h['Authorization'] = 'Bearer ' + t;
    }
    return h;
}

async function board1ApiFetch(url, options) {
    const opts = options || {};
    const headers = new Headers(opts.headers || {});
    const auth = board1AuthHeaders();
    Object.keys(auth).forEach(k => headers.set(k, auth[k]));
    if (opts.body && !(opts.body instanceof FormData) && !headers.has('Content-Type')) {
        headers.set('Content-Type', 'application/json');
    }
    return fetch(url, { ...opts, headers });
}

async function board1ParseJsonResponse(res) {
    const text = await res.text();
    if (!text) {
        return null;
    }
    try {
        return JSON.parse(text);
    }
    catch (e) {
        return { raw: text };
    }
}

async function board1ApiJson(method, url, bodyObj) {
    const opts = { method, headers: board1AuthHeaders() };
    if (bodyObj !== undefined && bodyObj !== null) {
        opts.headers = new Headers(opts.headers);
        opts.headers.set('Content-Type', 'application/json');
        opts.body = JSON.stringify(bodyObj);
    }
    const res = await fetch(url, opts);
    const data = await board1ParseJsonResponse(res);
    if (!res.ok) {
        const msg = data && (data.message || data.error) ? (data.message || data.error) : res.statusText;
        throw new Error(msg || ('HTTP ' + res.status));
    }
    return data;
}

function board1Redirect(url) {
    window.location.href = url;
}

/** 컨텍스트 경로 (루트 배포면 ''). Thymeleaf에서 window.BOARD1_CTX 설정 가능 */
function board1BasePath() {
    if (typeof window.BOARD1_CTX === 'string') {
        return window.BOARD1_CTX.endsWith('/') ? window.BOARD1_CTX.slice(0, -1) : window.BOARD1_CTX;
    }
    return '';
}

function board1Path(p) {
    const b = board1BasePath();
    if (!p.startsWith('/')) {
        p = '/' + p;
    }
    return b + p;
}

function board1InitNav() {
    const el = document.getElementById('nav-auth');
    if (!el) {
        return;
    }
    const w = board1Path('/ui/posts/write');
    const login = board1Path('/ui/login');
    const reg = board1Path('/ui/register');
    const posts = board1Path('/ui/posts');
    if (board1GetToken()) {
        el.innerHTML = '<a href="' + w + '">글쓰기</a> · <a href="#" id="board1-logout">로그아웃</a>';
        const lo = document.getElementById('board1-logout');
        if (lo) {
            lo.addEventListener('click', function (e) {
                e.preventDefault();
                board1ClearToken();
                board1Redirect(posts);
            });
        }
    }
    else {
        el.innerHTML = '<a href="' + login + '">로그인</a> · <a href="' + reg + '">회원가입</a> · <a href="' + w + '">글쓰기</a>';
    }
}

async function board1ApiGetJson(url) {
    const res = await board1ApiFetch(url, { method: 'GET' });
    const data = await board1ParseJsonResponse(res);
    if (!res.ok) {
        const msg = data && (data.message || data.error) ? (data.message || data.error) : res.statusText;
        throw new Error(msg || ('HTTP ' + res.status));
    }
    return data;
}

async function board1ApiSendForm(method, url, formData) {
    const headers = new Headers();
    const t = board1GetToken();
    if (t) {
        headers.set('Authorization', 'Bearer ' + t);
    }
    const res = await fetch(url, { method, body: formData, headers });
    const data = await board1ParseJsonResponse(res);
    if (!res.ok) {
        const msg = data && (data.message || data.error) ? (data.message || data.error) : res.statusText;
        throw new Error(msg || ('HTTP ' + res.status));
    }
    return data;
}

async function board1ApiDelete(url) {
    const res = await board1ApiFetch(url, { method: 'DELETE' });
    if (res.status === 204) {
        return;
    }
    const data = await board1ParseJsonResponse(res);
    if (!res.ok) {
        const msg = data && (data.message || data.error) ? (data.message || data.error) : res.statusText;
        throw new Error(msg || ('HTTP ' + res.status));
    }
}
