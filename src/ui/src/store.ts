import { create } from "zustand";

type AppState = {
  orgSlug: string | null;
  teamIdentifier: string | null;
  setContext: (orgSlug: string, teamIdentifier: string) => void;
};

export const useAppStore = create<AppState>((set) => ({
  orgSlug: null,
  teamIdentifier: null,
  setContext: (orgSlug, teamIdentifier) => set({ orgSlug, teamIdentifier })
}));
