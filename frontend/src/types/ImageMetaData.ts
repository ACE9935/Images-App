import type { CIFAR10Classes } from "./cifar10";

export interface ImageMetaData {
  id: number; 
  name: string;
  title:string,
  size:string,
  format:string,
  score?:number,
  imgUrl:string,
  imgClass:CIFAR10Classes,
  imgClassConfidence:number,
  author:string,
  imgUploadDate:string,
  likeCount:number,
  downloadCount:number,
}