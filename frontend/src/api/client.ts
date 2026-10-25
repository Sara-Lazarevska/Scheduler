import type {Booking, BookSlotRequest, Confirmation, CreateSlotRequest, Slot, SlotStatus} from './types';

const BASE = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

export class ApiError extends Error {
    status: number;
    fieldErrors?: Record<string, string>;
    constructor(status: number, message: string, fieldErrors?: Record<string, string>) {
        super(message);
        this.status = status;
        this.fieldErrors = fieldErrors;
    }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
    const res = await fetch(`${BASE}${path}`, {
        headers: { 'Content-Type': 'application/json' },
        ...init,
    });

    if (!res.ok) {
        let message = 'Something went wrong. Please try again.';
        let fieldErrors: Record<string, string> | undefined;
        try {
            const body = await res.json();
            message = body.detail ?? message;
            fieldErrors = body.errors;
        } catch {
            /* response had no JSON body */
        }
        throw new ApiError(res.status, message, fieldErrors);
    }

    if (res.status === 204) return undefined as T;
    return res.json();
}

export const api = {
    listSlots: (status?: SlotStatus) =>
        request<Slot[]>(`/api/slots${status ? `?status=${status}` : ''}`),
    createSlot: (body: CreateSlotRequest) =>
        request<Slot>('/api/slots', { method: 'POST', body: JSON.stringify(body) }),
    deleteSlot: (id: number) => request<void>(`/api/slots/${id}`, { method: 'DELETE' }),
    book: (slotId: number, body: BookSlotRequest) =>
        request<Booking>(`/api/slots/${slotId}/bookings`, { method: 'POST', body: JSON.stringify(body) }),
    listBookings: () => request<Booking[]>('/api/bookings'),
    getConfirmation: (bookingId: number) =>
        request<Confirmation>(`/api/bookings/${bookingId}/confirmation`, { method: 'POST' }),
};