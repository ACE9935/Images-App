export type Section = "upload-section" | "favorites-images-section" | "albums-section" | "history-section" | "uploaded-images-section";

export type SectionType = {
    id: Section;
    name: string;
    icon: string;
};