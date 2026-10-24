import type { Slot } from '../api/types';

export const hhmm = (t: string) => t.slice(0, 5);

export function endTime(start: string, minutes: number) {
    const [h, m] = start.split(':').map(Number);
    const total = h * 60 + m + minutes;
    const hh = String(Math.floor(total / 60) % 24).padStart(2, '0');
    const mm = String(total % 60).padStart(2, '0');
    return `${hh}:${mm}`;
}

export function formatDay(date: string) {
    return new Date(`${date}T00:00:00`).toLocaleDateString('en-GB', {
        weekday: 'long',
        day: 'numeric',
        month: 'long',
    });
}

export function groupByDate(slots: Slot[]) {
    return slots.reduce<Record<string, Slot[]>>((acc, s) => {
        (acc[s.date] ||= []).push(s);
        return acc;
    }, {});
}