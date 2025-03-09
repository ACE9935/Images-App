export interface Image {
    id: string; 
    name: string;
  }

export interface ImageMetadata {
    id: number; 
    name: string;
    size:string,
    format:string,
    score?:number,
  }