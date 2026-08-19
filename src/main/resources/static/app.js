const authView = document.querySelector('#auth-view');
const libraryView = document.querySelector('#library-view');
const message = document.querySelector('#message');
const bookList = document.querySelector('#book-list');
const usernameLabel = document.querySelector('#signed-in-as');

const session = {
  get token() { return sessionStorage.getItem('library-token'); },
  set token(value) { sessionStorage.setItem('library-token', value); },
  clear() { sessionStorage.removeItem('library-token'); sessionStorage.removeItem('library-username'); },
  get username() { return sessionStorage.getItem('library-username'); },
  set username(value) { sessionStorage.setItem('library-username', value); }
};

function showMessage(text, isError = false) {
  message.textContent = text;
  message.className = `message${isError ? ' error' : ''}`;
  message.hidden = false;
}

async function request(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...options.headers };
  if (session.token) headers.Authorization = `Bearer ${session.token}`;
  const response = await fetch(path, { ...options, headers });
  const body = response.status === 204 ? null : await response.json().catch(() => null);
  if (!response.ok) {
    if (response.status === 401) logout(false);
    throw new Error(body?.message || body?.error || `Request failed (${response.status})`);
  }
  return body;
}

function updateView() {
  const signedIn = Boolean(session.token);
  authView.hidden = signedIn;
  libraryView.hidden = !signedIn;
  usernameLabel.textContent = signedIn ? `Signed in as ${session.username}` : '';
  if (signedIn) loadBooks();
}

async function loadBooks() {
  bookList.textContent = 'Loading books…';
  try {
    const books = await request('/books');
    bookList.replaceChildren();
    if (books.length === 0) {
      const empty = document.createElement('p');
      empty.className = 'empty-state';
      empty.textContent = 'No books yet. Add the first one.';
      bookList.append(empty);
      return;
    }
    books.forEach((book) => {
      const row = document.createElement('article'); row.className = 'book-row';
      const details = document.createElement('div');
      const title = document.createElement('span'); title.className = 'book-title'; title.textContent = book.title;
      const author = document.createElement('span'); author.className = 'book-author'; author.textContent = book.authorName;
      details.append(title, author); row.append(details);
      const deleteButton = document.createElement('button');
      deleteButton.type = 'button'; deleteButton.className = 'delete-button'; deleteButton.textContent = 'Delete';
      deleteButton.addEventListener('click', () => deleteBook(book.id));
      row.append(deleteButton); bookList.append(row);
    });
  } catch (error) { showMessage(error.message, true); }
}

async function deleteBook(id) {
  try { await request(`/books/${id}`, { method: 'DELETE' }); showMessage('Book deleted.'); loadBooks(); }
  catch (error) { showMessage(error.message, true); }
}

function logout(announce = true) { session.clear(); updateView(); if (announce) showMessage('You have signed out.'); }

document.querySelector('#login-form').addEventListener('submit', async (event) => {
  event.preventDefault();
  const formElement = event.currentTarget;
  const form = new FormData(formElement);
  try {
    const result = await request('/auth/login', { method: 'POST', body: JSON.stringify(Object.fromEntries(form)) });
    session.token = result.token; session.username = form.get('username'); formElement.reset(); updateView(); showMessage('Welcome back.');
  } catch (error) { showMessage(error.message, true); }
});

document.querySelector('#register-form').addEventListener('submit', async (event) => {
  event.preventDefault();
  const formElement = event.currentTarget;
  const form = new FormData(formElement);
  try {
    const result = await request('/auth/register', { method: 'POST', body: JSON.stringify(Object.fromEntries(form)) });
    await request(`/auth/verify?token=${encodeURIComponent(result.verificationToken)}`);
    formElement.reset(); showMessage('Account created and verified. You can sign in now.');
  } catch (error) { showMessage(error.message, true); }
});

document.querySelector('#book-form').addEventListener('submit', async (event) => {
  event.preventDefault();
  const formElement = event.currentTarget;
  const form = new FormData(formElement);
  try {
    await request('/books', { method: 'POST', body: JSON.stringify({ title: form.get('title'), authorId: Number(form.get('authorId')) }) });
    formElement.reset(); showMessage('Book added.'); loadBooks();
  } catch (error) { showMessage(error.message, true); }
});

document.querySelector('#logout-button').addEventListener('click', () => logout());
document.querySelector('#refresh-button').addEventListener('click', loadBooks);
updateView();
