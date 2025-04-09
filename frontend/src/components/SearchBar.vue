<template>

    <Dialog v-model:visible="visibleDialog" modal header="Search for Images/Users" class="w-min">
      <div class="text-black flex flex-col gap-6">

        <form @submit="handleSearch" class="flex flex-col gap-2">
          <BasicInput
            maxLength="24"
            :style="{ width: '20rem' }"
            v-model="searchTerm"
            id="name-reset-input"
            label="Search..."
          />
          <BasicButton icon="pi-plus" type="submit">
            <i class="pi pi-search" style="font-size: 1rem"></i> Search
          </BasicButton>
        </form>
    
      </div>
    </Dialog>
    <Button v-tooltip.bottom="{ value: 'Search for Images/Users', autoHide: false, showDelay: 1000 }" v-bind="$attrs" size="large" @click="visibleDialog=true" icon="pi pi-search" variant="outlined" rounded aria-label="Search for Images or Users" />
</template>

<script setup lang="ts">
import { Dialog, Button } from 'primevue';
import BasicInput from './form/BasicInput.vue';
import BasicButton from './form/BasicButton.vue';
import { ref } from 'vue';
import router from '../router';

const visibleDialog=ref(false);
const searchTerm=ref<string>("");

const handleSearch = (e?: Event) => {
  e?.preventDefault(); // prevent page reload

  if (!searchTerm.value.trim()) return;

  visibleDialog.value = false; // close dialog
  router.push({ path: '/search', query: { term: searchTerm.value.trim() } }).catch(() => {});
};


</script>