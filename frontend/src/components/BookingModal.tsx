import { useState, type FormEvent } from 'react';
import { api, ApiError } from '../api/client';
import type { Booking, Slot } from '../api/types';
import { endTime, formatDay, hhmm } from '../utils/format';

interface Props {
    slot: Slot;
    onClose: () => void;
    onBooked: (booking: Booking) => void;
    onConflict: () => void;
}

export default function BookingModal({ slot, onClose, onBooked, onConflict }: Props) {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [notes, setNotes] = useState('');
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

    async function handleSubmit(e: FormEvent) {
        e.preventDefault();
        setSubmitting(true);
        setError(null);
        setFieldErrors({});
        try {
            const booking = await api.book(slot.id, { name, email, notes: notes || undefined });
            onBooked(booking);
        } catch (err) {
            if (err instanceof ApiError) {
                setError(err.message);
                setFieldErrors(err.fieldErrors ?? {});
                if (err.status === 409) onConflict();
            } else {
                setError('Could not reach the server. Please try again.');
            }
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <div className="overlay" onClick={onClose}>
    <form className="modal" onClick={(e) => e.stopPropagation()} onSubmit={handleSubmit}>
        <h2>Book your interview</h2>
    <p className="muted">
        {formatDay(slot.date)}, {hhmm(slot.startTime)} to {endTime(slot.startTime, slot.durationMinutes)}
    {slot.position && <> · {slot.position}</>}
    </p>

    <label>
    Full name
    <input value={name} onChange={(e) => setName(e.target.value)} required maxLength={100} />
        {fieldErrors.name && <span className="field-error">{fieldErrors.name}</span>}
            </label>

            <label>
            Email
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        {fieldErrors.email && <span className="field-error">{fieldErrors.email}</span>}
                </label>

                <label>
                Notes (optional)
                <textarea value={notes} onChange={(e) => setNotes(e.target.value)} rows={3} maxLength={1000} />
        </label>

            {error && <div className="alert">{error}</div>}

                <div className="actions">
            <button type="button" className="btn secondary" onClick={onClose}>Cancel</button>
                <button type="submit" className="btn" disabled={submitting}>
                {submitting ? 'Booking...' : 'Confirm booking'}
                </button>
                </div>
                </form>
                </div>
            );
            }