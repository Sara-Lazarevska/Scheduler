import { useCallback, useEffect, useState, type FormEvent } from 'react';
import { api, ApiError } from '../api/client';
import type { Booking, Slot } from '../api/types';
import { endTime, formatDay, hhmm } from '../utils/format';

const today = new Date().toISOString().slice(0, 10);

export default function HostPage() {
    const [slots, setSlots] = useState<Slot[]>([]);
    const [bookings, setBookings] = useState<Booking[]>([]);
    const [error, setError] = useState<string | null>(null);

    const [date, setDate] = useState(today);
    const [startTime, setStartTime] = useState('10:00');
    const [duration, setDuration] = useState(30);
    const [position, setPosition] = useState('');
    const [submitting, setSubmitting] = useState(false);

    const load = useCallback(async () => {
        try {
            const [s, b] = await Promise.all([api.listSlots(), api.listBookings()]);
            setSlots(s);
            setBookings(b);
        } catch {
            setError('Could not load data from the server.');
        }
    }, []);

    useEffect(() => {
        load();
    }, [load]);

    async function handleCreate(e: FormEvent) {
        e.preventDefault();
        setSubmitting(true);
        setError(null);
        try {
            await api.createSlot({ date, startTime, durationMinutes: duration, position: position || undefined });
            await load();
        } catch (err) {
            setError(err instanceof ApiError ? err.message : 'Could not create the slot.');
        } finally {
            setSubmitting(false);
        }
    }

    async function handleDelete(id: number) {
        setError(null);
        try {
            await api.deleteSlot(id);
            await load();
        } catch (err) {
            setError(err instanceof ApiError ? err.message : 'Could not delete the slot.');
        }
    }

    return (
        <>
            <h1>Host dashboard</h1>
            {error && <div className="alert">{error}</div>}

            <form className="card form-row" onSubmit={handleCreate}>
                <label>
                    Date
                    <input type="date" min={today} value={date} onChange={(e) => setDate(e.target.value)} required />
                </label>
                <label>
                    Start
                    <input type="time" value={startTime} onChange={(e) => setStartTime(e.target.value)} required />
                </label>
                <label>
                    Duration
                    <select value={duration} onChange={(e) => setDuration(Number(e.target.value))}>
                        {[15, 30, 45, 60].map((d) => (
                            <option key={d} value={d}>{d} min</option>
                        ))}
                    </select>
                </label>
                <label>
                    Position (optional)
                    <input value={position} onChange={(e) => setPosition(e.target.value)} placeholder="Java Intern" />
                </label>
                <button className="btn" disabled={submitting}>{submitting ? 'Adding...' : 'Add slot'}</button>
            </form>

            <h2>Slots</h2>
            <div className="card list">
                {slots.length === 0 && <p className="muted">No slots yet.</p>}
                {slots.map((s) => (
                    <div key={s.id} className="row">
                        <div>
                            <strong>{formatDay(s.date)}</strong>, {hhmm(s.startTime)} to {endTime(s.startTime, s.durationMinutes)}
                            {s.position && <span className="muted"> · {s.position}</span>}
                        </div>
                        <div className="row-right">
                            <span className={`badge ${s.status.toLowerCase()}`}>{s.status}</span>
                            {s.status === 'AVAILABLE' && (
                                <button className="btn small danger" onClick={() => handleDelete(s.id)}>Delete</button>
                            )}
                        </div>
                    </div>
                ))}
            </div>

            <h2>Bookings</h2>
            <div className="card list">
                {bookings.length === 0 && <p className="muted">No bookings yet.</p>}
                {bookings.map((b) => (
                    <div key={b.id} className="row">
                        <div>
                            <strong>{b.candidateName}</strong> <span className="muted">({b.candidateEmail})</span>
                            <div className="muted">
                                {formatDay(b.date)}, {hhmm(b.startTime)} to {endTime(b.startTime, b.durationMinutes)}
                                {b.position && <> · {b.position}</>}
                            </div>
                            {b.notes && <div className="note">"{b.notes}"</div>}
                        </div>
                    </div>
                ))}
            </div>
        </>
    );
}