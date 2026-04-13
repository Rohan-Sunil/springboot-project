const API = '/api/students';

const $ = (q) => document.querySelector(q);
const tbody = $('#students-table tbody');
const statusEl = $('#status');
const form = $('#student-form');
const idEl = $('#student-id');
const nameEl = $('#name');
const emailEl = $('#email');
const courseEl = $('#course');
const cancelBtn = $('#cancel-btn');
const refreshBtn = $('#refresh-btn');
const searchEl = $('#search');
const formTitle = $('#form-title');
const formErrors = $('#form-errors');

function setStatus(msg, kind='info') {
  statusEl.textContent = msg || '';
  statusEl.style.color = kind === 'error' ? '#ef4444' : '#94a3b8';
}

function setErrors(errors) {
  if (!errors || Object.keys(errors).length === 0) {
    formErrors.hidden = true;
    formErrors.textContent = '';
  } else {
    formErrors.hidden = false;
    formErrors.textContent = Object.entries(errors).map(([k,v]) => `${k}: ${v}`).join(' | ');
  }
}

function toRow(s) {
  const tr = document.createElement('tr');
  tr.innerHTML = `
    <td>${s.id}</td>
    <td>${s.name ?? ''}</td>
    <td>${s.email ?? ''}</td>
    <td>${s.course ?? ''}</td>
    <td>
      <button class="secondary" data-edit="${s.id}">Edit</button>
      <button class="danger" data-del="${s.id}">Delete</button>
    </td>
  `;
  return tr;
}

async function fetchJSON(url, opts) {
  const res = await fetch(url, {headers: {'Content-Type':'application/json'}, ...opts});
  if (!res.ok) {
    let error;
    try { error = await res.json(); } catch { error = { message: res.statusText }; }
    const err = new Error(error.message || 'Request failed');
    err.details = error.errors;
    err.status = res.status;
    throw err;
  }
  if (res.status === 204) return null;
  return res.json();
}

async function loadStudents() {
  setStatus('Loading...');
  try {
    const list = await fetchJSON(API);
    const q = searchEl.value?.toLowerCase().trim();
    tbody.innerHTML = '';
    (list || [])
      .filter(s => !q || [s.name, s.email, s.course].some(v => (v||'').toLowerCase().includes(q)))
      .forEach(s => tbody.appendChild(toRow(s)));
    setStatus(`${tbody.children.length} students`);
  } catch (e) {
    setStatus(e.message, 'error');
  }
}

function resetForm() {
  idEl.value = '';
  nameEl.value = '';
  emailEl.value = '';
  courseEl.value = '';
  formTitle.textContent = 'Add Student';
  setErrors(null);
}

tbody.addEventListener('click', async (e) => {
  const t = e.target;
  const editId = t.getAttribute('data-edit');
  const delId = t.getAttribute('data-del');
  if (editId) {
    try {
      const s = await fetchJSON(`${API}/${editId}`);
      idEl.value = s.id;
      nameEl.value = s.name ?? '';
      emailEl.value = s.email ?? '';
      courseEl.value = s.course ?? '';
      formTitle.textContent = 'Edit Student';
      window.scrollTo({top:0, behavior:'smooth'});
    } catch (e) {
      setStatus(e.message, 'error');
    }
  } else if (delId) {
    if (!confirm('Delete this student?')) return;
    try {
      await fetchJSON(`${API}/${delId}`, { method: 'DELETE' });
      await loadStudents();
    } catch (e) {
      setStatus(e.message, 'error');
    }
  }
});

form.addEventListener('submit', async (e) => {
  e.preventDefault();
  setErrors(null);
  const payload = {
    name: nameEl.value.trim(),
    email: emailEl.value.trim() || null,
    course: courseEl.value.trim() || null
  };
  const id = idEl.value;
  const method = id ? 'PUT' : 'POST';
  const url = id ? `${API}/${id}` : API;
  try {
    await fetchJSON(url, { method, body: JSON.stringify(payload) });
    resetForm();
    await loadStudents();
  } catch (e) {
    setErrors(e.details || null);
    setStatus(e.message, 'error');
  }
});

cancelBtn.addEventListener('click', () => resetForm());
refreshBtn.addEventListener('click', () => loadStudents());
searchEl.addEventListener('input', () => loadStudents());

loadStudents();
