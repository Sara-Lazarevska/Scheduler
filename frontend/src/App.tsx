import { BrowserRouter, NavLink, Route, Routes } from 'react-router-dom';
import BookPage from './pages/BookPage';
import HostPage from './pages/HostPage';

export default function App() {
  return (
      <BrowserRouter>
        <header className="header">
          <span className="brand">Interview Scheduler</span>
          <nav>
            <NavLink to="/" end>Book a slot</NavLink>
            <NavLink to="/host">Host</NavLink>
          </nav>
        </header>
        <main className="container">
          <Routes>
            <Route path="/" element={<BookPage />} />
            <Route path="/host" element={<HostPage />} />
          </Routes>
        </main>
      </BrowserRouter>
  );
}