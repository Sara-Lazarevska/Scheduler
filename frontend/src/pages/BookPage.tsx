import { useCallback, useEffect, useState } from 'react';
import { api } from '../api/client';
import type { Booking, Slot } from '../api/types';
import BookingModal from '../components/BookingModal';
import { endTime, formatDay, groupByDate, hhmm } from '../utils/format';

export default function BookPage() {
    const [slots, setSlots] = useState<Slot[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [selected, setSelected] = useState<Slot | null>(null);
    const [confirmed, setConfirmed] = useState<Booking | null>(null);

    const load = useCallback(async () => {
        try {
            setError(null);
            setSlots(await api.listSlots('AVAILABLE'));
        } catch {
            setError('Could not load slots. The server may be waking up, please try again in a few seconds.');
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        load();
    }, [load]);

    if (confirmed) {
        return (
            <div className="card center">
                <div className="check">✓</div>
                <h2>You're booked!</h2>
                <p>
                    {formatDay(confirmed.date)}, {hhmm(confirmed.startTime)} to{' '}
                    {endTime(confirmed.startTime, confirmed.durationMinutes)}
                    {confirmed.position && <> · {confirmed.position}</>}
                </p>
                <p className="muted">Booked as {confirmed.candidateName} ({confirmed.candidateEmail})</p>
                <button
                    className="btn"
                    onClick={() => {
                        setConfirmed(null);
                        load();
                    }}
                >
                    Back to slots
                </button>
            </div>
        );
    }

    const grouped = groupByDate(slots);

    return (
        <>
            <h1>Pick an interview slot</h1>
            <p className="muted">All times are in local time (Europe/Skopje).</p>

            {loading && <p>Loading slots...</p>}
            {error && (
                <div className="alert">
                    {error} <button className="link" onClick={load}>Retry</button>
                </div>
            )}
            {!loading && !error && slots.length === 0 && (
                <div className="card">No available slots right now. Please check back later.</div>
            )}

            {Object.entries(grouped).map(([date, daySlots]) => (
                <section key={date} className="day">
                    <h3>{formatDay(date)}</h3>
                    <div className="slot-grid">
                        {daySlots.map((s) => (
                            <button key={s.id} className="slot" onClick={() => setSelected(s)}>
                                <strong>{hhmm(s.startTime)}</strong>
                                <span>{s.durationMinutes} min</span>
                                {s.position && <small>{s.position}</small>}
                            </button>
                        ))}
                    </div>
                </section>
            ))}

            {selected && (
                <BookingModal
                    slot={selected}
                    onClose={() => setSelected(null)}
                    onBooked={(b) => {
                        setSelected(null);
                        setConfirmed(b);
                    }}
                    onConflict={load}
                />
            )}
        </>
    );
}