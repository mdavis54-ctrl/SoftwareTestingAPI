// Lab settings, served by the backend at /lab-config.js (see LabConfigController.java).
//
// latencyMinMs / latencyMaxMs: every API response is held back for a random time in this
//   range, to behave like a real network. Tests must wait for the page, not assume speed.
// mutant: the id of a deliberately faulty variant of the UI, used to check whether a test
//   suite notices when the UI breaks. Empty means the normal UI.
interface LabConfig {
    mutant?: string;
    latencyMinMs?: number;
    latencyMaxMs?: number;
}

declare global {
    interface Window {
        LAB_CONFIG?: LabConfig;
    }
}

const config: LabConfig = window.LAB_CONFIG ?? {};

export const isMutant = (id: string): boolean => config.mutant === id;

export function simulatedLatency(): Promise<void> {
    const min = config.latencyMinMs ?? 0;
    const max = Math.max(min, config.latencyMaxMs ?? min);
    const ms = min + Math.random() * (max - min);
    return new Promise(resolve => setTimeout(resolve, ms));
}

// Use this instead of fetch() for every call to the API.
export async function apiFetch(url: string, init?: RequestInit): Promise<Response> {
    const response = await fetch(url, init);
    await simulatedLatency();
    return response;
}
