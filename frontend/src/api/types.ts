export type SlotStatus = 'AVAILABLE' | 'BOOKED';

export interface Slot {
    id: number;
    date: string;
    startTime: string;
    durationMinutes: number;
    position: string | null;
    status: SlotStatus;
}

export interface CreateSlotRequest {
    date: string;
    startTime: string;
    durationMinutes: number;
    position?: string;
}

export interface BookSlotRequest {
    name: string;
    email: string;
    notes?: string;
}

export interface Booking {
    id: number;
    slotId: number;
    date: string;
    startTime: string;
    durationMinutes: number;
    position: string | null;
    candidateName: string;
    candidateEmail: string;
    notes: string | null;
    createdAt: string;
}